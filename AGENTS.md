# AGENTS.md

## Cursor Cloud specific instructions

### Product

Single-module Android app **Caderninho** (`:app`) — offline sales notebook (customers, orders, installments, “paying today”). No backend services; data is local Room + DataStore.

### Prerequisites (VM image)

- **JDK 17** — set `JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64` before Gradle (project `jvmTarget` / `compileOptions` are 17).
- **Android SDK** at `/opt/android-sdk` with platform **android-35**, **build-tools;35.0.0** (Gradle may also pull 34.0.0), **platform-tools**, and an emulator **system-images;android-35;google_apis;x86_64** if you need UI E2E.
- **`local.properties`** (gitignored) must contain `sdk.dir=/opt/android-sdk`. The VM update script recreates this file if missing.

### Gradle commands (from repo root)

| Task | Command |
|------|---------|
| Unit tests | `./gradlew :app:testDebugUnitTest` or `./gradlew test` |
| Lint | `./gradlew :app:lintDebug` or `./gradlew lint` |
| Debug APK | `./gradlew :app:assembleDebug` |
| Install on device/emulator | `./gradlew :app:installDebug` |
| Full verify + release assemble | `./gradlew build` |

See `app/build.gradle.kts` and `gradle/libs.versions.toml` for versions (Gradle 8.9, compileSdk 35).

### Emulator (cloud VM)

- **No KVM** in this environment — start with `-no-accel` and `-gpu swiftshader_indirect`. First boot often takes **~8–10 minutes** without hardware acceleration.
- Example AVD name used in setup: `caderninho_api35` (Pixel 6 profile, API 35 google_apis x86_64).
- Put `platform-tools` and `emulator` on `PATH` (`$ANDROID_HOME/platform-tools`, `$ANDROID_HOME/emulator`).
- Wait for boot: `adb wait-for-device` then `adb shell getprop sys.boot_completed` until `1`.
- Launch app: `adb shell am start -n caderninho.ethyios.net.br/com.caderninho.vendas.MainActivity`
- Onboarding can be skipped via UI **“pular”** (top-right); main screen is **Cobranças** (paying today).

### Gotchas

- **ANRs** on the emulator are common under software rendering; screens still load after dismissing dialogs.
- **Release signing** is optional (`CADERNINHO_RELEASE_*` in `local.properties` or env); debug builds need no keystore.
- **`_design_extract/`** is static HTML mockups only — not part of the Android build.
- No `androidTest` sources; `connectedDebugAndroidTest` has nothing to run.
