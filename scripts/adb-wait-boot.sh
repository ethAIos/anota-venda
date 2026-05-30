#!/usr/bin/env bash
set -euo pipefail

ANDROID_HOME="${ANDROID_HOME:-/opt/android-sdk}"
export PATH="$ANDROID_HOME/platform-tools:$PATH"

echo "Waiting for adb device..."
adb wait-for-device

echo "Waiting for Android boot to complete..."
for _ in $(seq 1 120); do
  boot="$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
  if [[ "$boot" == "1" ]]; then
    echo "Boot complete."
  adb shell settings put global window_animation_scale 0 >/dev/null 2>&1 || true
  adb shell settings put global transition_animation_scale 0 >/dev/null 2>&1 || true
  adb shell settings put global animator_duration_scale 0 >/dev/null 2>&1 || true
    exit 0
  fi
  sleep 5
done

echo "Timed out waiting for boot." >&2
exit 1
