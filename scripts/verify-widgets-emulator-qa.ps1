<#
.SYNOPSIS
    Boot emulator, install debug APK, warm demo seed, run launcher-first widget QA.

.NOTES
    Does not wipe emulator data by default (preserves manually placed home-screen widgets).
    Pass -WipeData only when you intentionally want a clean AVD (widgets must be re-pinned).
#>
param(
    [string]$AvdName = "caderninho_widget36",
    [string]$SdkRoot = "",
    [switch]$WipeData,
    [switch]$SkipInstall,
    [string]$LauncherPages = "2,3",
    [string]$OutputDir = "",
    [int]$WarmupSeconds = 5
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path $PSScriptRoot -Parent

if ([string]::IsNullOrWhiteSpace($SdkRoot)) {
    if ($env:ANDROID_HOME) { $SdkRoot = $env:ANDROID_HOME }
    else { $SdkRoot = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
}

$env:ANDROID_HOME = $SdkRoot
$env:ANDROID_SDK_ROOT = $SdkRoot
$env:PATH = @(
    (Join-Path $SdkRoot "platform-tools"),
    (Join-Path $SdkRoot "emulator"),
    (Join-Path $SdkRoot "cmdline-tools\latest\bin"),
    $env:PATH
) -join [IO.Path]::PathSeparator

Write-Host "=== Widget emulator QA ==="
Write-Host "SDK: $SdkRoot"
if ($WipeData) {
    Write-Host "WARN: -WipeData removes launcher widgets; re-place A1/A2/A3 after this run."
}

$serial = & (Join-Path $PSScriptRoot "android-emulator.ps1") -AvdName $AvdName -SdkRoot $SdkRoot -WipeData:$WipeData -WaitForBoot | Select-Object -Last 1
if (-not $serial -or $serial -notmatch "^emulator-\d+$") {
    throw "No running emulator found after boot (got: '$serial')."
}
Write-Host "Using device: $serial"

if (-not $SkipInstall) {
    $javaHome = $env:JAVA_HOME
    if (-not $javaHome -or -not (Test-Path $javaHome)) {
        $candidate = "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"
        if (Test-Path $candidate) { $env:JAVA_HOME = $candidate }
    }
    $env:ANDROID_SERIAL = $serial
    Push-Location $repoRoot
    try {
        & .\gradlew.bat :app:installDebug "-Pandroid.injected.device.serial=$serial"
        if ($LASTEXITCODE -ne 0) { throw "installDebug failed with exit $LASTEXITCODE" }
    } finally {
        Pop-Location
    }
    Write-Host "Installed debug APK on $serial."
}

$deviceQaArgs = @{
    Serial        = $serial
    LauncherPages = $LauncherPages
    WarmupSeconds = $WarmupSeconds
}
if (-not [string]::IsNullOrWhiteSpace($OutputDir)) {
    $deviceQaArgs.OutputDir = $OutputDir
}

& (Join-Path $PSScriptRoot "verify-widgets-device-qa.ps1") @deviceQaArgs
