# DejaVu

Here lives **DejaVu** — an app first imagined in 2015 and published to the Play Store as my first app. As life progressed, it was left unmaintained. Now it is resurrected: a Kotlin Multiplatform + Compose Multiplatform rewrite for Android and iOS.

Think of a word. Don’t say it out loud. Walk through a two-step letter-group puzzle, and DejaVu reveals what you were thinking.

## Project layout

```
androidApp/     Android app
iosApp/         Xcode / iOS app
shared/         Shared logic + Compose UI
docs/           Process docs (releases, etc.)
maestro/        UI flow used to capture store screenshots
scripts/        Store screenshot capture / staging
store_assets/   Raw screenshot captures, icon, feature graphic
```

## Setup

**Requirements**

- JDK 17+ (CI uses 21)
- Android SDK (`compileSdk` 36, `minSdk` 24)
- macOS + Xcode 15+ for iOS (deployment target iOS 15+)

**Clone and configure**

```bash
git clone https://github.com/theVinesh/dejavu-app.git
cd dejavu-app
```

Create `local.properties` at the repo root (Android Studio usually writes this):

```properties
sdk.dir=/path/to/Android/sdk
```

Open the project in Android Studio, or use the Gradle wrapper from the CLI.

## Run

**Android**

```bash
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug
```

Or run the `androidApp` configuration from Android Studio.

**iOS**

1. Open `iosApp/iosApp.xcodeproj` in Xcode.
2. Confirm `TEAM_ID` in `iosApp/Configuration/Config.xcconfig` if needed.
3. Pick a simulator or device, then build & run.

Xcode builds the shared framework via:

```bash
./gradlew :shared:embedAndSignAppleFrameworkForXcode
```

## Test

```bash
./gradlew :shared:allTests
# Android unit tests only:
./gradlew :shared:testDebugUnitTest
# JVM suite (also used in Deploy CI):
./gradlew :shared:jvmTest
```

PR CI runs Android and iOS build/test workflows. Publishing is separate — see [docs/release_process.md](docs/release_process.md).

## Contribute

1. Fork / branch from `main`.
2. Keep changes focused; match existing Kotlin/Compose style in `shared/`.
3. Run the tests above before opening a PR.
4. Prefer small PRs with a clear why in the description.

Questions or ideas: open an issue on the repo.

## Release

Pushes to `main` (or a manual Deploy run) ship signed builds to Play internal testing and TestFlight. Secrets, versioning, and store listing sync are documented in **[docs/release_process.md](docs/release_process.md)**.
