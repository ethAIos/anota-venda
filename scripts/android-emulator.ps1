param(
    [string]$AvdName = "caderninho_widget36",
    [string]$SdkRoot = "",
    [switch]$WipeData,
    [switch]$WaitForBoot
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($SdkRoot)) {
    if ($env:ANDROID_HOME) { $SdkRoot = $env:ANDROID_HOME }
    elseif ($env:ANDROID_SDK_ROOT) { $SdkRoot = $env:ANDROID_SDK_ROOT }
    else { $SdkRoot = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
}

$emulator = Join-Path $SdkRoot "emulator\emulator.exe"
if (-not (Test-Path $emulator)) {
    throw "Android emulator not found at $emulator"
}

$avds = & $emulator -list-avds 2>&1
if ($avds -notcontains $AvdName) {
    $createScript = Join-Path $PSScriptRoot "create-android-avd.ps1"
    & $createScript -AvdName $AvdName -SdkRoot $SdkRoot
}

$emuArgs = @(
    "-avd", $AvdName,
    "-no-snapshot-save",
    "-no-boot-anim",
    "-no-audio",
    "-no-metrics"
)
if ($WipeData) { $emuArgs += "-wipe-data" }

$accel = & $emulator -accel-check 2>&1 | Out-String
if ($accel -match "HAXM|WHPX|AEHD|accel") {
    Write-Host "Hardware acceleration available."
    $emuArgs += @("-gpu", "auto")
} else {
    Write-Host "WARNING: No hardware acceleration detected. Emulator will be slow."
    $emuArgs += @("-gpu", "swiftshader_indirect")
}

function Get-EmulatorSerial {
    return (& adb devices 2>&1 | Select-String "^(emulator-\d+)\s+device$" | ForEach-Object {
        $_.Matches[0].Groups[1].Value
    } | Select-Object -First 1)
}

function Wait-ForEmulatorOnline {
    param([string]$Serial, [int]$TimeoutSeconds = 120)
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        $state = (& adb -s $Serial get-state 2>&1 | Out-String).Trim()
        if ($state -eq "device") { return }
        Start-Sleep -Seconds 3
    }
    throw "Emulator $Serial did not reach online state."
}

function Wait-ForEmulatorSerial {
    param([int]$TimeoutSeconds = 300)
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        $serial = Get-EmulatorSerial
        if ($serial) { return $serial }
        Start-Sleep -Seconds 3
    }
    throw "Timed out waiting for emulator serial in adb devices."
}

$serial = Get-EmulatorSerial
if ($serial -and -not $WipeData) {
    try {
        Wait-ForEmulatorOnline -Serial $serial -TimeoutSeconds 15
        Write-Host "Emulator already connected: $serial"
        if ($WaitForBoot) {
            & (Join-Path $PSScriptRoot "adb-wait-boot.ps1") -Serial $serial
        }
        Write-Output $serial
        exit 0
    } catch {
        Write-Host "Stale emulator entry ($serial); starting a new instance."
        $serial = $null
    }
}

Write-Host "Starting emulator $AvdName ..."
$process = Start-Process -FilePath $emulator -ArgumentList $emuArgs -PassThru
Write-Host "Emulator PID: $($process.Id)"

if ($WaitForBoot) {
    $serial = Wait-ForEmulatorSerial
    Wait-ForEmulatorOnline -Serial $serial
    & (Join-Path $PSScriptRoot "adb-wait-boot.ps1") -Serial $serial
    Write-Output $serial
}
