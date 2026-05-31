param(
    [string]$Serial = "",
    [int]$TimeoutSeconds = 300
)

$ErrorActionPreference = "Stop"

$adbArgs = @("wait-for-device")
if ($Serial) { $adbArgs = @("-s", $Serial) + $adbArgs }

Write-Host "Waiting for adb device..."
& adb @adbArgs | Out-Null

$deadline = (Get-Date).AddSeconds($TimeoutSeconds)
Write-Host "Waiting for Android boot to complete..."
while ((Get-Date) -lt $deadline) {
    $bootArgs = @("shell", "getprop", "sys.boot_completed")
    if ($Serial) { $bootArgs = @("-s", $Serial) + $bootArgs }
    $boot = (& adb @bootArgs 2>$null | Out-String).Trim()
    if ($boot -eq "1") {
        Write-Host "Boot complete."
        $settingsArgs = @("shell", "settings", "put", "global")
        foreach ($pair in @(
            @("window_animation_scale", "0"),
            @("transition_animation_scale", "0"),
            @("animator_duration_scale", "0")
        )) {
            $cmd = $settingsArgs + $pair
            if ($Serial) { $cmd = @("-s", $Serial) + $cmd }
            & adb @cmd | Out-Null
        }
        exit 0
    }
    Start-Sleep -Seconds 5
}

throw "Timed out waiting for boot after ${TimeoutSeconds}s."
