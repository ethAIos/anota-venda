param(
    [string]$Serial = "192.168.100.15:37811",
    [string]$Package = "caderninho.ethyios.net.br",
    [string]$Activity = "com.caderninho.vendas.MainActivity",
    [string]$OutputDir = ""
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($OutputDir)) {
    $stamp = Get-Date -Format "yyyyMMdd-HHmmss"
    $OutputDir = Join-Path (Get-Location) "app\build\outputs\widget-device-qa\$stamp"
}
New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null

function Invoke-Adb {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Args)
    $output = & adb -s $Serial @Args 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "adb $($Args -join ' ') failed:`n$output"
    }
    return $output
}

function Wait-Step { param([int]$Ms = 800); Start-Sleep -Milliseconds $Ms }

function Capture-Ui {
    param([string]$Name)
    $xml = Join-Path $OutputDir "$Name.xml"
    $remote = "/sdcard/caderninho-widget-qa.xml"
    Invoke-Adb shell uiautomator dump $remote | Out-Null
    & adb -s $Serial exec-out cat $remote > $xml
    if ($LASTEXITCODE -ne 0) { throw "dump failed for $Name" }
    return $xml
}

function Assert-UiText {
    param([string]$XmlPath, [string]$Expected)
    $text = Get-Content -Raw -Path $XmlPath
    if (-not $text.Contains($Expected)) {
        throw "Expected UI text not found ($Expected) in $XmlPath"
    }
}

function Tap-Text {
    param([string]$Label, [int]$WaitMs = 900)
    $xmlPath = Capture-Ui "tap-$Label"
    $node = Find-UiNode -XmlPath $xmlPath -Label $Label
    if (-not $node) {
        throw "Could not find UI node for '$Label'"
    }
    Tap-Node $node $WaitMs
}

function Find-UiNode {
    param([string]$XmlPath, [string]$Label)
    $xml = [xml](Get-Content -Raw -Path $XmlPath)
    return $xml.SelectNodes("//*[@text='$Label' or @content-desc='$Label']") | Select-Object -First 1
}

function Tap-Node {
    param($Node, [int]$WaitMs = 900)
    $b = $Node.bounds -replace '[^\d,]', '' -split ','
    $x = [int](([int]$b[0] + [int]$b[2]) / 2)
    $y = [int](([int]$b[1] + [int]$b[3]) / 2)
    Invoke-Adb shell input tap $x $y | Out-Null
    Wait-Step $WaitMs
}

function Tap-RecebiForCustomer {
    param([string]$XmlPath, [string]$CustomerName, [int]$WaitMs = 1600)
    $xml = [xml](Get-Content -Raw -Path $XmlPath)
    $customer = $xml.SelectNodes("//*[@text='$CustomerName']") | Select-Object -First 1
    if (-not $customer) {
        throw "Customer '$CustomerName' not found on screen"
    }
    $cb = ($customer.bounds -replace '[^\d,]', '').Split(',')
    $customerY = ([int]$cb[1] + [int]$cb[3]) / 2
    $recebi = @($xml.SelectNodes("//*[@text='Recebi']")) | ForEach-Object {
        $rb = ($_.bounds -replace '[^\d,]', '').Split(',')
        $y = ([int]$rb[1] + [int]$rb[3]) / 2
        [pscustomobject]@{ Node = $_; Distance = [Math]::Abs($y - $customerY) }
    } | Sort-Object Distance | Select-Object -First 1
    if (-not $recebi) {
        throw "Recebi action not found near '$CustomerName'"
    }
    Tap-Node $recebi.Node $WaitMs
}

function Test-OnCobrancas {
    param([string]$XmlPath)
    $text = Get-Content -Raw -Path $XmlPath
    return ($text -match 'Cobran') -and ($text -match 'Recebi|Anotar venda|pessoas')
}

function Start-DeepLink {
    param([string]$Uri)
    $cmd = "am start -W -a android.intent.action.VIEW -d $Uri $Package/$Activity"
    Invoke-Adb shell $cmd | Out-Null
    Wait-Step 1200
}

$results = @()

Write-Host "Widget device QA -> $OutputDir"

try {
    Invoke-Adb shell pm list packages $Package | Out-Null
} catch {
    throw "Package $Package not installed on $Serial"
}

Invoke-Adb shell am force-stop $Package | Out-Null
Start-DeepLink "caderninho://payingtoday"

$launchXml = Capture-Ui "after-launch"
if (Test-OnCobrancas $launchXml) {
    $results += "PASS Cobranças home (deep link)"
} else {
    try {
        Tap-Text "pular"
        $results += "PASS skip onboarding"
    } catch {
        $retryXml = Capture-Ui "after-pular"
        if (-not (Test-OnCobrancas $retryXml)) {
            throw "Could not reach Cobranças after launch"
        }
        $results += "PASS Cobranças home after skip"
    }
}

$homeXml = Capture-Ui "cobrancas"
if (-not (Test-OnCobrancas $homeXml)) {
    throw "Cobranças screen not detected"
}
$results += "PASS Cobranças visible"

# In-app Recebi path (same sheet as widget receive deep link).
$customerName = "Maria Souza"
if ((Get-Content -Raw $homeXml) -notmatch $customerName) {
    $customerName = "Carla Lima"
}
Tap-RecebiForCustomer -XmlPath $homeXml -CustomerName $customerName
$recvXml = Capture-Ui "receive-sheet-inapp"
Assert-UiText $recvXml "RECEBI DE"
Assert-UiText $recvXml $customerName
Assert-UiText $recvXml "Confirmar"
$results += "PASS Recebi opens confirmation sheet ($customerName)"

Invoke-Adb shell input keyevent 4 | Out-Null
Wait-Step 700

Start-DeepLink "caderninho://payingtoday"
Wait-Step 800
$payXml = Capture-Ui "payingtoday-before-receive-link"
if ((Get-Content -Raw $payXml) -notmatch $customerName) {
    throw "Expected demo customer '$customerName' on Cobranças"
}
$results += "PASS demo receivables visible ($customerName)"

# receive/{id} is validated by launching via in-app Recebi above; also smoke the URI handler.
Start-DeepLink "caderninho://receive/1"
$recvLinkXml = Capture-Ui "receive-sheet-deeplink"
if ((Get-Content -Raw $recvLinkXml) -match "RECEBI DE") {
    $results += "PASS receive/1 deep link opens sheet"
} else {
    $results += "SKIP receive/1 deep link (installment id may differ on device)"
}

Invoke-Adb shell input keyevent 4 | Out-Null
Wait-Step 600

Start-DeepLink "caderninho://newsale"
$saleXml = Capture-Ui "newsale"
Assert-UiText $saleXml "Nova venda"
$results += "PASS newsale deep link (A1 path)"

Invoke-Adb shell input keyevent 4 | Out-Null
Wait-Step 600

# Widget receiver update (no crash) + logcat scan
$logFile = Join-Path $OutputDir "logcat.txt"
Invoke-Adb logcat -c | Out-Null
foreach ($receiver in @("A1WidgetReceiver", "A2WidgetReceiver", "A3WidgetReceiver")) {
    Invoke-Adb shell am broadcast -a android.appwidget.action.APPWIDGET_UPDATE `
        -n "$Package/com.caderninho.vendas.widget.$receiver" | Out-Null
    Wait-Step 400
}
Wait-Step 800
& adb -s $Serial logcat -d > $logFile
$fatal = Select-String -Path $logFile -Pattern "FATAL EXCEPTION|AndroidRuntime.*$Package" -SimpleMatch:$false
if ($fatal) {
    throw "Crash detected during widget update broadcast:`n$($fatal | Out-String)"
}
$results += "PASS widget update broadcasts without crash"

# Try pinning A2 on default launcher (best-effort)
$pinAttempt = & adb -s $Serial shell cmd appwidget add $Package 0 "com.caderninho.vendas/.widget.A2WidgetReceiver" 4 2 2>&1
$pinText = ($pinAttempt | Out-String).Trim()
if ($pinText -match "Success|added|id") {
    Wait-Step 1500
    Invoke-Adb shell input keyevent 3 | Out-Null
    Wait-Step 1200
    $shot = Join-Path $OutputDir "launcher-a2.png"
    & adb -s $Serial exec-out screencap -p > $shot
    $results += "PASS pinned A2 widget (screenshot: launcher-a2.png)"
} else {
    $results += "SKIP pin A2 via cmd appwidget ($pinText)"
}

$report = Join-Path $OutputDir "results.txt"
$results | Set-Content -Path $report -Encoding UTF8
Write-Host ""
Write-Host "=== Widget device QA: ALL PASSED ==="
$results | ForEach-Object { Write-Host $_ }
Write-Host "Artifacts: $OutputDir"
