# DejaVu (Kotlin Multiplatform)

A modern rewrite of the 2015 DejaVu word-puzzle app using **Kotlin Multiplatform** and **Compose Multiplatform**, targeting Android and iOS.

## What it does

1. **Splash** – zoom-in brand intro  
2. **Word Count** – enter how many letters are in the word you are thinking of  
3. **Step 1** – alphabet is split into groups; tap groups in letter order (with undo)  
4. **Step 2** – groups are transposed; tap again in order  
5. **Result** – diagonal extraction reveals the word; tap to bounce it around with sound  

Facebook SDK, Parse, AdMob, and other analytics from the original app were removed.

## Project structure

```
androidApp/     # Android application entry point
iosApp/         # Xcode / iOS application
shared/         # Shared business logic + Compose Multiplatform UI
gradle/         # Version catalog (libs.versions.toml)
```

## Requirements

### Android
- JDK 17+
- Android SDK (compileSdk 36, minSdk 24)
- Android Studio Otter / recent stable, or command-line Gradle

### iOS
- macOS with Xcode 15+
- iOS 14+ deployment target
- Cocoa / Xcode command-line tools

## Setup

1. Clone this repository.
2. Create `local.properties` in the project root (Android Studio usually does this):

```properties
sdk.dir=/path/to/Android/sdk
```

3. Open the project in Android Studio, or use the Gradle wrapper from the command line.

## Build & run – Android

```bash
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug
```

Or open the project in Android Studio and run the `androidApp` configuration.

## Build & run – iOS

1. Open `iosApp/iosApp.xcodeproj` in Xcode.  
2. Set your development team in `iosApp/Configuration/Config.xcconfig` (`TEAM_ID`) if needed.  
3. Select an iOS Simulator or device.  
4. Build & run.  

Xcode invokes Gradle to produce the `Shared` framework via:

```bash
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

You can also generate the framework from the command line on a Mac before opening Xcode.

## Tests

```bash
./gradlew :shared:allTests
# or Android unit tests only:
./gradlew :shared:testDebugUnitTest
```

## CI & release

GitHub Actions on every push/PR to `main`:

- **Android CI** (`.github/workflows/android.yml`) – unit tests + `assembleDebug`
- **iOS CI** (`.github/workflows/ios.yml`) – shared framework + Xcode simulator build

On push to `main` (and manual `workflow_dispatch`):

- **Deploy** (`.github/workflows/deploy.yml`) – signed Android AAB → Play **internal** (`completed`, not draft) and iOS IPA → **TestFlight**

See [docs/release_process.md](docs/release_process.md) for required secrets and one-time store setup.

## Architecture notes

- **MVVM** with shared `ViewModel`s and `StateFlow` UI state  
- **Koin** for dependency injection  
- **Compose Navigation** (type-safe routes) for Splash → WordCount → Step1 → Step2 → Word  
- **multiplatform-settings** for the first-launch tutorial flag  
- **expect/actual-style platform module** for sound (`SoundPool` on Android, `AVAudioPlayer` on iOS)  
- Result bounce uses a lightweight Compose physics loop (not Box2D)  
- Custom font `circula.otf` and assets migrated from the original app  

## Color palette (from original)

| Token          | Hex       |
|----------------|-----------|
| background     | `#c0392b` |
| lighterTheme   | `#e74c3c` |
| primaryText    | `#ecf0f1` |
| secondaryText  | `#95a5a6` |
| yellow         | `#f1c40f` |
| green          | `#16a085` |
