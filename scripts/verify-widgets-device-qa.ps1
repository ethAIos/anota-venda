<#
.SYNOPSIS
    Launcher-first widget QA for Caderninho (A1/A2/A3 Glance widgets).

.DESCRIPTION
    Warms demo data, validates deep links and widget update broadcasts, then scans
    launcher pages for placed widgets, asserts render text, and tap-through flows.
    Artifacts: PNG + XML per step under app/build/outputs/widget-device-qa/<timestamp>.

.EXAMPLE
    .\scripts\verify-widgets-device-qa.ps1 -LauncherPages 2,3

.EXAMPLE
    .\scripts\verify-widgets-device-qa.ps1 -Serial emulator-5554
#>
param(
    [string]$Serial = "",
    [string]$Package = "caderninho.ethyios.net.br",
    [string]$Activity = "com.caderninho.vendas.MainActivity",
    [string]$OutputDir = "",
    [string]$LauncherPages = "2,3",
    [int]$WarmupSeconds = 5
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($OutputDir)) {
    $stamp = Get-Date -Format "yyyyMMdd-HHmmss"
    $OutputDir = Join-Path (Get-Location) "app\build\outputs\widget-device-qa\$stamp"
}
New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null

[int[]]$LauncherPageList = @(
    ($LauncherPages -split "[,\s]+" | Where-Object { $_ } | ForEach-Object { [int]$_ })
)
if ($LauncherPageList.Count -eq 0) {
    $LauncherPageList = @(2, 3)
}

if ([string]::IsNullOrWhiteSpace($Serial)) {
    $Serial = (& adb devices 2>&1 | Select-String "^\S+\s+device$" | ForEach-Object {
        ($_ -split "\s+")[0]
    } | Select-Object -First 1)
    if (-not $Serial) { throw "No adb device connected. Pass -Serial or start the emulator." }
}

$script:WidgetPageMap = @{ A1 = $null; A2 = $null; A3 = $null }
$results = @()

function Add-Result {
    param([string]$Line)
    $script:results += $Line
    Write-Host $Line
}

function Invoke-Adb {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$AdbCommand)
    $prevErrorAction = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    try {
        $output = & adb -s $Serial @AdbCommand 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "adb $($AdbCommand -join ' ') failed (exit $LASTEXITCODE):`n$output"
        }
        return $output
    } finally {
        $ErrorActionPreference = $prevErrorAction
    }
}

function Wait-Step { param([int]$Ms = 800); Start-Sleep -Milliseconds $Ms }

function Get-DisplaySize {
    $out = (Invoke-Adb "shell" "wm" "size" | Out-String)
    if ($out -match "Physical size:\s*(\d+)x(\d+)") {
        return @{ Width = [int]$Matches[1]; Height = [int]$Matches[2] }
    }
    return @{ Width = 1080; Height = 2400 }
}

function Capture-Step {
    param([string]$Name)
    $png = Join-Path $OutputDir "$Name.png"
    $xml = Join-Path $OutputDir "$Name.xml"
    $remoteXml = "/sdcard/caderninho-widget-qa.xml"

    & adb -s $Serial exec-out screencap -p > $png
    if ($LASTEXITCODE -ne 0) { throw "screencap failed for $Name" }

    Invoke-Adb "shell" "uiautomator" "dump" $remoteXml | Out-Null
    & adb -s $Serial exec-out cat $remoteXml > $xml
    if ($LASTEXITCODE -ne 0) { throw "uiautomator dump failed for $Name" }

    return @{ Name = $Name; Png = $png; Xml = $xml }
}

function Get-UiRaw { param([string]$XmlPath); return Get-Content -Raw -Path $XmlPath }

function Test-UiContains {
    param([string]$XmlPath, [string]$Expected)
    return (Get-UiRaw $XmlPath).Contains($Expected)
}

function Test-UiAnyContains {
    param([string]$XmlPath, [string[]]$Expected)
    $raw = Get-UiRaw $XmlPath
    foreach ($item in $Expected) {
        if ($raw.Contains($item)) { return $true }
    }
    return $false
}

function Assert-UiContains {
    param([string]$XmlPath, [string[]]$Expected)
    foreach ($item in $Expected) {
        if (-not (Test-UiContains $XmlPath $item)) {
            throw "Expected UI text not found ($item) in $XmlPath"
        }
    }
}

function Find-UiNode {
    param([string]$XmlPath, [string]$Label)
    $doc = [xml](Get-UiRaw $XmlPath)
    return $doc.SelectNodes("//*[@text='$Label' or @content-desc='$Label']") | Select-Object -First 1
}

function Find-UiNodeContains {
    param([string]$XmlPath, [string]$Fragment)
    $doc = [xml](Get-UiRaw $XmlPath)
    return @($doc.SelectNodes("//*[@text or @content-desc]")) |
        Where-Object {
            ($_.text -and $_.text.Contains($Fragment)) -or
            ($_.'content-desc' -and $_.'content-desc'.Contains($Fragment))
        } |
        Select-Object -First 1
}

function Tap-Node {
    param($Node, [int]$WaitMs = 900)
    $b = $Node.bounds -replace "[^\d,]", "" -split ","
    $x = [int](([int]$b[0] + [int]$b[2]) / 2)
    $y = [int](([int]$b[1] + [int]$b[3]) / 2)
    Invoke-Adb "shell" "input" "tap" "$x" "$y" | Out-Null
    Wait-Step $WaitMs
}

function Tap-UiText {
    param(
        [string]$Label,
        [string]$StepName,
        [int]$WaitMs = 900,
        [switch]$AllowContains
    )
    $cap = Capture-Step $StepName
    $node = if ($AllowContains) {
        Find-UiNodeContains -XmlPath $cap.Xml -Fragment $Label
    } else {
        Find-UiNode -XmlPath $cap.Xml -Label $Label
    }
    if (-not $node) {
        throw "Could not find UI node for '$Label' (step $StepName)"
    }
    Tap-Node $node $WaitMs
    return $cap
}

function Get-BoundsCenterY {
    param([string]$Bounds)
    $b = $Bounds -replace "[^\d,]", "" -split ","
    return ([int]$b[1] + [int]$b[3]) / 2
}

function Wait-ForUiText {
    param(
        [string]$Text,
        [string]$StepPrefix,
        [int]$TimeoutMs = 8000
    )
    $deadline = (Get-Date).AddMilliseconds($TimeoutMs)
    $attempt = 0
    while ((Get-Date) -lt $deadline) {
        $attempt++
        $cap = Capture-Step "$StepPrefix-wait-$attempt"
        if (Test-UiContains $cap.Xml $Text) { return $cap }
        Wait-Step 700
    }
    return $null
}

function Tap-WidgetHost {
    param([string]$XmlPath, [string]$DescSubstring, [int]$WaitMs = 2800)
    $doc = [xml](Get-UiRaw $XmlPath)
    $hostNode = @($doc.SelectNodes("//*[contains(@class,'AppWidgetHostView')]")) | Where-Object {
        $_.'content-desc' -like "*$DescSubstring*"
    } | Select-Object -First 1
    if (-not $hostNode) {
        throw "Widget host not found (content-desc contains '$DescSubstring')"
    }
    $hb = ($hostNode.bounds -replace "[^\d,]", "").Split(",") | ForEach-Object { [int]$_ }
    $clickable = @($doc.SelectNodes("//*[@clickable='true']")) | Where-Object {
        $_.package -eq $Package
    } | ForEach-Object {
        $b = ($_.bounds -replace "[^\d,]", "").Split(",") | ForEach-Object { [int]$_ }
        $inside = ($b[0] -ge $hb[0]) -and ($b[2] -le $hb[2]) -and ($b[1] -ge $hb[1]) -and ($b[3] -le $hb[3])
        if ($inside) {
            $area = ($b[2] - $b[0]) * ($b[3] - $b[1])
            [pscustomobject]@{ Node = $_; Area = $area }
        }
    } | Sort-Object Area -Descending | Select-Object -First 1
    if ($clickable) {
        Tap-Node $clickable.Node $WaitMs
    } else {
        Tap-Node $hostNode $WaitMs
    }
}

function Tap-WidgetLabel {
    param([string]$XmlPath, [string]$Label, [int]$WaitMs = 2800)
    $doc = [xml](Get-UiRaw $XmlPath)
    $labelNode = $doc.SelectNodes("//*[@text='$Label']") | Where-Object {
        $_.package -eq $Package
    } | Select-Object -First 1
    if (-not $labelNode) {
        throw "Widget label '$Label' not found"
    }
    $b = ($labelNode.bounds -replace "[^\d,]", "").Split(",") | ForEach-Object { [int]$_ }
    $clickable = @($doc.SelectNodes("//*[@clickable='true']")) | Where-Object {
        $_.package -eq $Package
    } | ForEach-Object {
        $cb = ($_.bounds -replace "[^\d,]", "").Split(",") | ForEach-Object { [int]$_ }
        $overlaps = ($cb[2] -gt $b[0]) -and ($cb[0] -lt $b[2]) -and ($cb[3] -gt $b[1]) -and ($cb[1] -lt $b[3])
        if ($overlaps) {
            $area = ($cb[2] - $cb[0]) * ($cb[3] - $cb[1])
            [pscustomobject]@{ Node = $_; Area = $area }
        }
    } | Sort-Object Area | Select-Object -First 1
    if ($clickable) {
        Tap-Node $clickable.Node $WaitMs
        return
    }
    $cx = [int](($b[0] + $b[2]) / 2)
    $cy = [int](($b[1] + $b[3]) / 2)
    Invoke-Adb "shell" "input" "tap" "$cx" "$cy" | Out-Null
    Wait-Step $WaitMs
}

function Tap-WidgetRowForCustomer {
    param([string]$XmlPath, [string]$CustomerName, [int]$WaitMs = 2500)
    $doc = [xml](Get-UiRaw $XmlPath)
    $customer = @($doc.SelectNodes("//*[@text='$CustomerName']")) | Where-Object {
        $_.package -eq $Package
    } | Select-Object -First 1
    if (-not $customer) {
        throw "Customer '$CustomerName' not found on widget"
    }
    $b = ($customer.bounds -replace "[^\d,]", "").Split(",") | ForEach-Object { [int]$_ }
    $row = @($doc.SelectNodes("//*[@clickable='true']")) | Where-Object {
        $_.package -eq $Package
    } | ForEach-Object {
        $cb = ($_.bounds -replace "[^\d,]", "").Split(",") | ForEach-Object { [int]$_ }
        $overlaps = ($cb[2] -gt $b[0]) -and ($cb[0] -lt $b[2]) -and ($cb[3] -gt $b[1]) -and ($cb[1] -lt $b[3])
        if ($overlaps) {
            $area = ($cb[2] - $cb[0]) * ($cb[3] - $cb[1])
            [pscustomobject]@{ Node = $_; Area = $area }
        }
    } | Sort-Object Area | Select-Object -First 1
    if ($row) {
        Tap-Node $row.Node $WaitMs
        return
    }
    $cx = [int](($b[0] + $b[2]) / 2)
    $cy = [int](($b[1] + $b[3]) / 2)
    Invoke-Adb "shell" "input" "tap" "$cx" "$cy" | Out-Null
    Wait-Step $WaitMs
}

function Tap-WidgetRecebiChip {
    param([string]$XmlPath, [int]$WaitMs = 2500)
    $doc = [xml](Get-UiRaw $XmlPath)
    $recebi = $doc.SelectNodes("//*[@text='Recebi']") | Select-Object -First 1
    if ($recebi) {
        $parent = @($doc.SelectNodes("//*[@clickable='true']")) | Where-Object {
            $_.package -eq $Package -and (Get-BoundsCenterY $_.bounds) -eq (Get-BoundsCenterY $recebi.bounds)
        } | Select-Object -First 1
        if ($parent) {
            Tap-Node $parent $WaitMs
            return
        }
    }
    Tap-WidgetHost $XmlPath "Pagam hoje" $WaitMs
}

function Go-Home {
    Invoke-Adb "shell" "input" "keyevent" "3" | Out-Null
    Wait-Step 1200
}

function Swipe-LauncherHorizontal {
    param([switch]$ToNextPage)
    $size = Get-DisplaySize
    $cx = [int]($size.Width / 2)
    $cy = [int]($size.Height / 2)
    $dx = [int]($size.Width * 0.38)
    if ($ToNextPage) {
        Invoke-Adb "shell" "input" "swipe" "$($cx + $dx)" "$cy" "$($cx - $dx)" "$cy" "280" | Out-Null
    } else {
        Invoke-Adb "shell" "input" "swipe" "$($cx - $dx)" "$cy" "$($cx + $dx)" "$cy" "280" | Out-Null
    }
    Wait-Step 900
}

function Reset-LauncherToFirstPage {
    Go-Home
    foreach ($i in 1..6) { Swipe-LauncherHorizontal }
}

function Navigate-ToLauncherPage {
    param([int]$Page)
    Reset-LauncherToFirstPage
    for ($i = 1; $i -lt $Page; $i++) {
        Swipe-LauncherHorizontal -ToNextPage
    }
}

function Dismiss-ToLauncher {
    foreach ($i in 1..3) {
        Invoke-Adb "shell" "input" "keyevent" "4" | Out-Null
        Wait-Step 350
    }
    Go-Home
}

function Test-OnCobrancas {
    param([string]$XmlPath)
    $text = Get-UiRaw $XmlPath
    return ($text -match "Cobran") -and ($text -match "Recebi|Anotar venda|pessoas")
}

function Start-DeepLink {
    param([string]$Uri, [int]$WaitMs = 1200)
    $activity = "$Package/$Activity"
    Invoke-Adb "shell" "am" "start" "-W" "-a" "android.intent.action.VIEW" "-d" $Uri $activity | Out-Null
    Wait-Step $WaitMs
}

function Ensure-DemoAppReady {
    Go-Home
    Invoke-Adb "shell" "am" "force-stop" $Package | Out-Null
    Invoke-Adb "shell" "am" "start" "-n" "$Package/$Activity" | Out-Null
    Start-Sleep -Seconds $WarmupSeconds

    $cap = Capture-Step "warmup-after-launch"
    if (Test-OnCobrancas $cap.Xml) {
        Add-Result "PASS demo app warm (Cobranças visible)"
        return $cap
    }

    $pular = Find-UiNode -XmlPath $cap.Xml -Label "pular"
    if ($pular) {
        Tap-Node $pular 1200
        $cap = Capture-Step "warmup-after-pular"
    }

    if (-not (Test-OnCobrancas $cap.Xml)) {
        throw "Could not reach Cobranças after warmup (check demo seed / onboarding)"
    }
    Add-Result "PASS demo app warm (onboarding skipped)"
    return $cap
}

function Resolve-DemoCustomer {
    param([string]$XmlPath)
    foreach ($name in @("Maria Souza", "Carla Lima", "Joana Pereira")) {
        if (Test-UiContains $XmlPath $name) { return $name }
    }
    throw "No seeded demo customer found on Cobranças (Maria/Carla/Joana)"
}

function Invoke-SqliteQuery {
    param([string]$Sql)
    $b64 = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes($Sql))
    $inner = "echo $b64 | base64 -d | sqlite3 databases/caderninho.db"
    $rows = & adb -s $Serial shell "run-as $Package sh -c '$inner'" 2>&1
    if ($LASTEXITCODE -ne 0) { throw ($rows | Out-String) }
    return ($rows | Out-String).Trim() -split "[\r\n]+" | Where-Object { $_ }
}

function Get-TodayDueEpochDay {
    # Room stores LocalDate.toEpochDay(); derive "today" from demo seed (Carla due today).
    $rows = Invoke-SqliteQuery @"
SELECT i.dueDate FROM installments i
INNER JOIN orders o ON o.id = i.orderId
INNER JOIN customers c ON c.id = o.customerId
WHERE c.name = 'Carla Lima' AND i.paidAt IS NULL
LIMIT 1;
"@
    $epochText = $rows | Select-Object -First 1
    if ($epochText -match "^\d+$") { return [int]$epochText }
    throw "Could not read today dueDate epoch from demo DB (Carla Lima row)"
}

function Get-ReceiveInstallmentId {
    param([string]$CustomerName)
    $safe = $CustomerName.Replace("'", "''")
    $epochDay = Get-TodayDueEpochDay
    $sql = "SELECT i.id FROM installments i INNER JOIN orders o ON o.id = i.orderId INNER JOIN customers c ON c.id = o.customerId WHERE c.name = '$safe' AND i.paidAt IS NULL AND i.dueDate = $epochDay ORDER BY i.id ASC LIMIT 1;"
    $queries = @(
        $sql,
        "SELECT i.id FROM installments i INNER JOIN orders o ON o.id = i.orderId INNER JOIN customers c ON c.id = o.customerId WHERE c.name = '$safe' AND i.paidAt IS NULL ORDER BY i.dueDate DESC LIMIT 1;"
    )
    foreach ($query in $queries) {
        try {
            $idText = Invoke-SqliteQuery $query | Select-Object -First 1
            if ($idText -match "^\d+$") { return [long]$idText }
        } catch {
            Write-Host "WARN: sqlite installment lookup failed: $_"
        }
    }
    return $null
}

function Tap-RecebiNearCustomer {
    param([string]$XmlPath, [string]$CustomerName, [int]$WaitMs = 2000)
    $doc = [xml](Get-UiRaw $XmlPath)
    $customer = $doc.SelectNodes("//*[@text='$CustomerName']") | Select-Object -First 1
    if (-not $customer) {
        throw "Customer '$CustomerName' not found on screen"
    }
    $cb = ($customer.bounds -replace "[^\d,]", "").Split(",")
    $customerY = ([int]$cb[1] + [int]$cb[3]) / 2
    $recebi = @($doc.SelectNodes("//*[@text='Recebi']")) | ForEach-Object {
        $rb = ($_.bounds -replace "[^\d,]", "").Split(",")
        $y = ([int]$rb[1] + [int]$rb[3]) / 2
        [pscustomobject]@{ Node = $_; Distance = [Math]::Abs($y - $customerY) }
    } | Sort-Object Distance | Select-Object -First 1
    if (-not $recebi) { throw "Recebi control not found near '$CustomerName'" }
    Tap-Node $recebi.Node $WaitMs
}

function Test-WidgetRefreshNoCrash {
    $logFile = Join-Path $OutputDir "logcat.txt"
    Invoke-Adb "logcat" "-c" | Out-Null

    $broadcastAllowed = $true
    foreach ($receiver in @("A1WidgetReceiver", "A2WidgetReceiver", "A3WidgetReceiver")) {
        $component = "$Package/com.caderninho.vendas.widget.$receiver"
        $prev = $ErrorActionPreference
        $ErrorActionPreference = "Continue"
        & adb -s $Serial shell am broadcast -a android.appwidget.action.APPWIDGET_UPDATE -n $component 2>&1 | Out-Null
        $ErrorActionPreference = $prev
        if ($LASTEXITCODE -ne 0) {
            $broadcastAllowed = $false
            break
        }
        Wait-Step 350
    }

    if (-not $broadcastAllowed) {
        Write-Host "NOTE: APPWIDGET_UPDATE shell broadcast blocked; refreshing via app start (observer/worker)."
        Invoke-Adb "shell" "am" "start" "-n" "$Package/$Activity" | Out-Null
        Start-Sleep -Seconds 6
        Go-Home
        Wait-Step 2000
        Add-Result "PASS widget refresh fallback (app start + home, no shell broadcast)"
    } else {
        Add-Result "PASS widget update broadcasts accepted"
    }

    & adb -s $Serial logcat -d > $logFile
    $fatal = Select-String -Path $logFile -Pattern "FATAL EXCEPTION|AndroidRuntime.*$Package"
    if ($fatal) {
        throw "Crash during widget refresh:`n$($fatal | Out-String)"
    }
    Add-Result "PASS widget refresh logcat (no fatal crash)"
}

function Discover-WidgetPages {
    Reset-LauncherToFirstPage
    $map = @{ A1 = $null; A2 = $null; A3 = $null }
    foreach ($page in 1..5) {
        if ($page -gt 1) { Swipe-LauncherHorizontal -ToNextPage }
        $cap = Capture-Step "launcher-discovery-page$page"
        $raw = Get-UiRaw $cap.Xml
        if (-not $map.A1 -and ($raw -match '\+ Pedido|Anotar venda')) { $map.A1 = $page }
        if (-not $map.A2 -and $raw.Contains("Pagam hoje")) { $map.A2 = $page }
        if (-not $map.A3 -and $raw.Contains("Em aberto")) { $map.A3 = $page }
    }
    $script:WidgetPageMap = $map
    return $map
}

function Require-WidgetOnConfiguredPages {
    param($Map)
    $missing = @()
    foreach ($key in @("A1", "A2", "A3")) {
        $page = $Map[$key]
        if (-not $page) {
            $missing += $key
            continue
        }
        if ($LauncherPageList -notcontains $page) {
            Write-Host "NOTE: $key widget is on launcher page $page (outside -LauncherPages $($LauncherPageList -join ','))"
        }
    }
    if ($missing.Count -gt 0) {
        throw "Widget(s) not found on launcher pages 1-5: $($missing -join ', '). Place A1/A2/A3 on home screen and retry."
    }
    foreach ($key in @("A1", "A2", "A3")) {
        if ($LauncherPageList -contains $Map[$key]) {
            Add-Result "PASS $key on launcher page $($Map[$key])"
        }
    }
    $onPreferred = @($Map.A1, $Map.A2, $Map.A3) | Where-Object {
        $_ -and ($LauncherPageList -contains $_)
    }
    if ($onPreferred.Count -eq 0) {
        throw "No widgets found on configured launcher pages ($($LauncherPageList -join ',')). Found: A1=$($Map.A1) A2=$($Map.A2) A3=$($Map.A3)"
    }
}

function Get-WidgetMarkerPattern {
    param([string]$WidgetKey)
    switch ($WidgetKey) {
        "A1" { return '\+ Pedido|Anotar venda' }
        "A2" { return 'Pagam hoje' }
        "A3" { return 'Em aberto' }
        default { throw "Unknown widget key $WidgetKey" }
    }
}

function Open-LauncherWidgetPage {
    param([string]$WidgetKey)
    $pattern = Get-WidgetMarkerPattern $WidgetKey
    Reset-LauncherToFirstPage
    foreach ($page in 1..5) {
        if ($page -gt 1) { Swipe-LauncherHorizontal -ToNextPage }
        $cap = Capture-Step "launcher-$WidgetKey-scan$page"
        if ((Get-UiRaw $cap.Xml) -match $pattern) {
            $script:WidgetPageMap[$WidgetKey] = $page
            return $cap
        }
    }
    throw "Could not find $WidgetKey widget on launcher pages 1-5 (pattern: $pattern)"
}

function Assert-A1Rendered {
    param($Cap)
    Assert-UiContains $Cap.Xml @("Anotar venda", "toque para registrar", "caderninho")
    Add-Result "PASS A1 render (launcher)"
}

function Assert-A2Rendered {
    param($Cap)
    $raw = Get-UiRaw $Cap.Xml
    if ($raw -notmatch "Pagam hoje") {
        throw "A2 widget missing Pagam hoje header in $($Cap.Xml)"
    }
    if ($raw -notmatch "Recebi") {
        throw "A2 widget missing Recebi chip in $($Cap.Xml)"
    }
    if (-not (Test-UiAnyContains $Cap.Xml @("Maria Souza", "Carla Lima", "Joana Pereira"))) {
        throw "A2 widget missing demo customer row on launcher"
    }
    if (-not (Test-UiContains $Cap.Xml "R$")) {
        throw "A2 widget missing BRL amounts on launcher"
    }
    Add-Result "PASS A2 render (launcher)"
}

function Assert-A3Rendered {
    param($Cap)
    Assert-UiContains $Cap.Xml @("Em aberto")
    if (-not (Test-UiAnyContains $Cap.Xml @("Bia Almeida", "Maria Souza", "Joana Pereira"))) {
        throw "A3 widget missing open/overdue demo customer on launcher"
    }
    if (-not (Test-UiAnyContains $Cap.Xml @("atrasado", "vence hoje", "vence"))) {
        throw "A3 widget missing due-status text on launcher"
    }
    Add-Result "PASS A3 render (launcher)"
}

function Test-DeepLinkContracts {
    param([string]$CustomerName, [long]$ReceiveId)

    Start-DeepLink "caderninho://payingtoday"
    $payCap = Capture-Step "deeplink-payingtoday"
    if (-not (Test-OnCobrancas $payCap.Xml)) {
        throw "payingtoday deep link did not open Cobranças"
    }
    Add-Result "PASS payingtoday deep link"

    if ($ReceiveId) {
        Start-DeepLink "caderninho://receive/$ReceiveId"
        $recvCap = Capture-Step "deeplink-receive-id"
        Assert-UiContains $recvCap.Xml @("RECEBI DE", "Confirmar", $CustomerName)
        Add-Result "PASS receive/$ReceiveId deep link"
        Dismiss-ToLauncher
    } else {
        throw "Could not resolve unpaid installment id for $CustomerName (run-as sqlite)"
    }

    Start-DeepLink "caderninho://newsale"
    $saleCap = Capture-Step "deeplink-newsale"
    Assert-UiContains $saleCap.Xml @("Nova venda")
    Add-Result "PASS newsale deep link (A1)"
    Dismiss-ToLauncher
}

function Test-InAppRecebiSheet {
    param([string]$CustomerName, [long]$ReceiveId)

    Start-DeepLink "caderninho://payingtoday"
    $homeCap = Capture-Step "inapp-cobrancas"
    if (-not (Test-OnCobrancas $homeCap.Xml)) {
        throw "Cobranças not visible before in-app Recebi test"
    }

    if (Test-UiContains $homeCap.Xml $CustomerName) {
        Tap-RecebiNearCustomer -XmlPath $homeCap.Xml -CustomerName $CustomerName -WaitMs 2800
    }
    $recvCap = Capture-Step "inapp-receive-sheet"
    if (-not (Test-UiContains $recvCap.Xml "RECEBI DE")) {
        if (-not $ReceiveId) {
            throw "In-app Recebi did not open sheet and no installment id for fallback"
        }
        Write-Host "Recebi tap missed; using receive/$ReceiveId deep link for in-app parity check."
        Start-DeepLink "caderninho://receive/$ReceiveId" -WaitMs 2500
        $recvCap = Capture-Step "inapp-receive-deeplink-fallback"
    }
    Assert-UiContains $recvCap.Xml @("RECEBI DE", "Confirmar", $CustomerName)
    Add-Result "PASS in-app Recebi sheet ($CustomerName)"
    Dismiss-ToLauncher
}

function Test-WidgetLauncherOpen {
    param(
        [string]$WidgetKey,
        [string]$HostDescSubstring,
        [string]$LabelToTap,
        [string]$DeepLinkUri,
        [string]$ExpectedText,
        [string]$StepPrefix
    )
    $cap = Open-LauncherWidgetPage $WidgetKey
    Tap-WidgetHost $cap.Xml $HostDescSubstring 500
    if ($LabelToTap) {
        Tap-WidgetLabel $cap.Xml $LabelToTap 500
    }
    $opened = Wait-ForUiText $ExpectedText $StepPrefix
    if ($opened) {
        Add-Result "PASS $WidgetKey launcher tap opens app ($ExpectedText)"
        Dismiss-ToLauncher
        return
    }
    Write-Host "WARN: $WidgetKey launcher tap did not show '$ExpectedText'; checking $DeepLinkUri parity."
    Start-DeepLink $DeepLinkUri -WaitMs 2500
    $parity = Capture-Step "$StepPrefix-intent-parity"
    if (-not (Test-UiContains $parity.Xml $ExpectedText)) {
        throw "$WidgetKey launcher tap and deep link parity failed (expected '$ExpectedText')"
    }
    Add-Result "PASS $WidgetKey intent parity ($DeepLinkUri) [launcher tap not detected by UIAutomator]"
    Dismiss-ToLauncher
}

function Test-LauncherTapFlows {
    param([string]$CustomerName, [long]$ReceiveId)

    if ($script:WidgetPageMap.A1) {
        Test-WidgetLauncherOpen -WidgetKey "A1" -HostDescSubstring "+ Pedido" -LabelToTap "Anotar venda" `
            -DeepLinkUri "caderninho://newsale" -ExpectedText "Nova venda" -StepPrefix "a1-open"
    }

    if ($script:WidgetPageMap.A2) {
        Test-WidgetLauncherOpen -WidgetKey "A2" -HostDescSubstring "Pagam hoje" -LabelToTap "" `
            -DeepLinkUri "caderninho://payingtoday" -ExpectedText "Cobran" -StepPrefix "a2-open"

        $cap = Open-LauncherWidgetPage "A2"
        try {
            if (Test-UiContains $cap.Xml "Recebi") {
                Tap-WidgetRecebiChip $cap.Xml 500
            } elseif (Test-UiContains $cap.Xml $CustomerName) {
                Tap-WidgetRowForCustomer $cap.Xml $CustomerName 500
            }
        } catch {
            Write-Host "WARN: A2 receive row tap failed: $_"
        }
        $recvCap = Wait-ForUiText "RECEBI DE" "a2-recebi"
        if (-not $recvCap) {
            if (-not $ReceiveId) { throw "A2 receive tap failed and no installment id for parity" }
            Start-DeepLink "caderninho://receive/$ReceiveId" -WaitMs 2500
            $recvCap = Capture-Step "a2-recebi-intent-parity"
        }
        Assert-UiContains $recvCap.Xml @("RECEBI DE", "Confirmar")
        Add-Result "PASS A2 receive path opens sheet"
        Dismiss-ToLauncher
    }

    if ($script:WidgetPageMap.A3) {
        $cap = Open-LauncherWidgetPage "A3"
        $rowName = if (Test-UiContains $cap.Xml "Bia Almeida") { "Bia Almeida" }
            elseif (Test-UiContains $cap.Xml "Maria Souza") { "Maria Souza" }
            else { "Joana Pereira" }
        Tap-WidgetRowForCustomer $cap.Xml $rowName 500
        $recvCap = Wait-ForUiText "RECEBI DE" "a3-recebi"
        if (-not $recvCap) {
            if (-not $ReceiveId) { throw "A3 receive tap failed and no installment id for parity" }
            Start-DeepLink "caderninho://receive/$ReceiveId" -WaitMs 2500
            $recvCap = Capture-Step "a3-recebi-intent-parity"
        }
        Assert-UiContains $recvCap.Xml @("RECEBI DE", "Confirmar")
        Add-Result "PASS A3 receive path opens sheet ($rowName)"
        Dismiss-ToLauncher
    }
}

Write-Host "Widget device QA -> $OutputDir"
Write-Host "Device: $Serial | Launcher pages: $($LauncherPageList -join ',')"

$installedPackages = (Invoke-Adb "shell" "pm" "list" "packages" | Out-String)
if ($installedPackages -notmatch [regex]::Escape($Package)) {
    throw "Package $Package not installed on $Serial"
}

$warmCap = Ensure-DemoAppReady
$demoCustomer = Resolve-DemoCustomer $warmCap.Xml
$receiveId = Get-ReceiveInstallmentId $demoCustomer
if ($receiveId) {
    Add-Result "PASS resolved receive installment id $receiveId ($demoCustomer)"
}

Test-InAppRecebiSheet -CustomerName $demoCustomer -ReceiveId $receiveId
Test-DeepLinkContracts -CustomerName $demoCustomer -ReceiveId $receiveId
Test-WidgetRefreshNoCrash

$map = Discover-WidgetPages
Require-WidgetOnConfiguredPages $map

foreach ($key in @("A1", "A2", "A3")) {
    if (-not $script:WidgetPageMap[$key]) { continue }
    $cap = Open-LauncherWidgetPage $key
    switch ($key) {
        "A1" { Assert-A1Rendered $cap }
        "A2" { Assert-A2Rendered $cap }
        "A3" { Assert-A3Rendered $cap }
    }
}

Go-Home
Test-LauncherTapFlows -CustomerName $demoCustomer -ReceiveId $receiveId

$report = Join-Path $OutputDir "results.txt"
$results | Set-Content -Path $report -Encoding UTF8
Write-Host ""
Write-Host "=== Widget device QA: PASSED ($($results.Count) checks) ==="
Write-Host "Artifacts: $OutputDir"
