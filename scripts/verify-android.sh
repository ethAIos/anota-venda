#!/usr/bin/env bash
# Recommended verification in cloud / CI — no emulator required.
set -euo pipefail

cd "$(dirname "$0")/.."
export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk-amd64}"
test -f local.properties || printf 'sdk.dir=%s\n' "${ANDROID_HOME:-/opt/android-sdk}" > local.properties

./gradlew check
