param(
    [string]$AvdName = "caderninho_widget36",
    [string]$DeviceProfile = "pixel_6",
    [string]$SystemImage = "system-images;android-36;google_apis;x86_64",
    [string]$SdkRoot = ""
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($SdkRoot)) {
    if ($env:ANDROID_HOME) { $SdkRoot = $env:ANDROID_HOME }
    elseif ($env:ANDROID_SDK_ROOT) { $SdkRoot = $env:ANDROID_SDK_ROOT }
    else { $SdkRoot = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
}

$emulator = Join-Path $SdkRoot "emulator\emulator.exe"
$avdManager = Join-Path $SdkRoot "cmdline-tools\latest\bin\avdmanager.bat"
$sdkManager = Join-Path $SdkRoot "cmdline-tools\latest\bin\sdkmanager.bat"

if (-not (Test-Path $emulator)) {
    throw "Android emulator not found at $emulator. Install Android SDK Emulator in SDK Manager."
}
if (-not (Test-Path $avdManager)) {
    throw "avdmanager not found at $avdManager. Install Android SDK Command-line Tools."
}

$existing = & $emulator -list-avds 2>&1
if ($existing -contains $AvdName) {
    Write-Host "AVD already exists: $AvdName"
    exit 0
}

Write-Host "Installing system image (if needed): $SystemImage"
& $sdkManager $SystemImage | Out-Host

Write-Host "Creating AVD: $AvdName"
"no" | & $avdManager create avd -n $AvdName -k $SystemImage -d $DeviceProfile --force | Out-Host
Write-Host "Created $AvdName"
