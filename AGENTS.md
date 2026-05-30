# AGENTS.md

## Cursor Cloud specific instructions

### Product

Single-module Android app **Caderninho** (`:app`) — offline sales notebook (customers, orders, installments, “paying today”). No backend services; data is local Room + DataStore.

### Prerequisites (VM image)

- **JDK 17** — `export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64` before Gradle.
- **Android SDK** at `/opt/android-sdk` (platform **android-35**, build-tools, **platform-tools**).
- **`local.properties`** (gitignored): `sdk.dir=/opt/android-sdk` (recreated by the VM update script if missing).

### Recommended verification (no emulator)

Cloud VMs here have **no `/dev/kvm`**. The Android emulator runs in pure software mode: boot takes many minutes, and **“App isn’t responding” / ANR dialogs are expected** for Google system services and often for debug builds. **Do not rely on the emulator for interactive development in this environment.**

Use instead:

```bash
./scripts/verify-android.sh   # runs ./gradlew check (unit tests + lint + assemble)
```

Or individually: `./gradlew :app:testDebugUnitTest`, `./gradlew :app:lintDebug`, `./gradlew :app:assembleDebug`.

### Interactive UI development (feasible options)

| Approach | Notes |
|----------|--------|
| **Physical Android device** | Best option. Enable developer options + USB debugging, connect via USB or `adb connect <ip>:5555`, then `./gradlew :app:installDebug`. |
| **Local machine with KVM** | Android Studio emulator on Linux/macOS/Windows with hardware acceleration (`/dev/kvm` or Hyper-V). |
| **Cloud emulator** | **Not recommended** — only for rare smoke tests; use `scripts/android-emulator.sh` (ATD image) and expect ANRs. |

After `adb devices` shows a real device:

```bash
./gradlew :app:installDebug
adb shell am start -n caderninho.ethyios.net.br/com.caderninho.vendas.MainActivity
```

Onboarding: tap **“pular”** (top-right) → main screen **Cobranças**.

### Emulator scripts (optional, slow without KVM)

- `scripts/android-emulator.sh` — starts AVD `caderninho_atd34` (API 34 **Google ATD** image, lighter than full `google_apis`).
- `scripts/adb-wait-boot.sh` — waits for boot and disables window animations.

Requires SDK packages: `system-images;android-34;google_atd;x86_64`, `emulator`, `platform-tools`.

### Gotchas

- **Release signing** is optional (`CADERNINHO_RELEASE_*` in `local.properties` or env).
- **`_design_extract/`** is design HTML only, not part of the Android build.
- No `androidTest` sources; `connectedDebugAndroidTest` has nothing to run.
- Widget refresh is **deferred 3s after process start** to keep cold start lighter on slow hardware.
