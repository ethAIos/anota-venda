#!/usr/bin/env bash
# Start an Android emulator tuned for this repo.
# Without KVM (/dev/kvm), interactive use is unreliable — prefer a physical device (see AGENTS.md).
set -euo pipefail

ANDROID_HOME="${ANDROID_HOME:-/opt/android-sdk}"
export ANDROID_HOME ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

AVD_NAME="${AVD_NAME:-caderninho_atd34}"
HAS_KVM=false
if [[ -r /dev/kvm ]] && "$ANDROID_HOME/emulator/emulator" -accel-check 2>/dev/null | grep -q "KVM"; then
  HAS_KVM=true
fi

EMU_ARGS=(
  -avd "$AVD_NAME"
  -no-snapshot-save
  -no-boot-anim
  -no-audio
  -no-metrics
)

if $HAS_KVM; then
  echo "KVM available — using hardware acceleration."
  EMU_ARGS+=(-gpu auto)
else
  echo "WARNING: No KVM. Software emulation is slow and apps may show ANR dialogs."
  echo "  Recommended: connect a physical device (adb devices) or develop on a host with KVM."
  echo "  For CI/agents, use: ./gradlew check   (no emulator required)"
  EMU_ARGS+=(-no-accel -gpu swiftshader_indirect)
fi

exec emulator "${EMU_ARGS[@]}" "$@"
