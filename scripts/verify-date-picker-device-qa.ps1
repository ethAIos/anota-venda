param(
    [string]$Serial = "192.168.1.145:37771",
    [string]$Package = "caderninho.ethyios.net.br",
    [string]$OutputDir = "",
    [switch]$SaveSale
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($OutputDir)) {
    $stamp = Get-Date -Format "yyyyMMdd-HHmmss"
    $OutputDir = Join-Path (Get-Location) "app\build\outputs\date-picker-device-qa\$stamp"
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

function Wait-Step {
    param([int]$Milliseconds = 1000)
    Start-Sleep -Milliseconds $Milliseconds
}

function Capture-Step {
    param([string]$Name)

    $png = Join-Path $OutputDir "$Name.png"
    $xml = Join-Path $OutputDir "$Name.xml"
    $remoteXml = "/sdcard/caderninho-date-picker-window.xml"

    & adb -s $Serial exec-out screencap -p > $png
    if ($LASTEXITCODE -ne 0) {
        throw "screencap failed for $Name"
    }

    Invoke-Adb shell uiautomator dump $remoteXml | Out-Null
    & adb -s $Serial exec-out cat $remoteXml > $xml
    if ($LASTEXITCODE -ne 0) {
        throw "uiautomator dump failed for $Name"
    }

    Write-Host "captured $Name"
    return @{ Png = $png; Xml = $xml }
}

function Assert-UiText {
    param(
        [string]$XmlPath,
        [string]$Expected
    )

    $xmlText = Get-Content -Raw -Path $XmlPath
    if (-not $xmlText.Contains($Expected)) {
        throw "Expected text not found in ${XmlPath}: $Expected"
    }
}

function Tap {
    param([int]$X, [int]$Y, [int]$WaitMs = 700)
    Invoke-Adb shell input tap $X $Y | Out-Null
    Wait-Step $WaitMs
}

function Key {
    param([int]$Code, [int]$WaitMs = 500)
    Invoke-Adb shell input keyevent $Code | Out-Null
    Wait-Step $WaitMs
}

function Text {
    param([string]$Value, [int]$WaitMs = 500)
    Invoke-Adb shell input text $Value | Out-Null
    Wait-Step $WaitMs
}

Write-Host "output: $OutputDir"
Write-Host "device: $Serial"

Invoke-Adb shell am force-stop $Package | Out-Null
Invoke-Adb shell am start "-W" "-a" android.intent.action.VIEW "-d" "caderninho://newsale" $Package | Out-Null
Wait-Step 2500
$capture = Capture-Step "01-newsale-open"
Assert-UiText $capture.Xml "Nova venda"
Assert-UiText $capture.Xml "02/06"

Tap 210 395
Text "QACliente"
Key 61
Key 61
Text "11977776666"
Key 61
Text "QAProduto"
Key 61
Text "12345"
Key 4 1000
$capture = Capture-Step "02-form-filled"
Assert-UiText $capture.Xml "QACliente"
Assert-UiText $capture.Xml "11977776666"
Assert-UiText $capture.Xml "QAProduto"
Assert-UiText $capture.Xml "123,45"

Tap 610 1740 1200
$capture = Capture-Step "03-picker-open"
Assert-UiText $capture.Xml "2 de jun. de 2026"

Tap 340 1538 800
$capture = Capture-Step "04-picker-selected-15"
Assert-UiText $capture.Xml "15 de junho de 2026"

Tap 857 2077 1200
$capture = Capture-Step "05-date-applied"
Assert-UiText $capture.Xml "15/06"

if ($SaveSale) {
    Tap 610 2310 2500
    $capture = Capture-Step "06-sale-saved"
    Assert-UiText $capture.Xml "Anotado!"
    Assert-UiText $capture.Xml "até 15/06."
}

Write-Host "date picker device QA passed"
