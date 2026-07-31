# Release process

Push to `main` (or run the **Deploy** workflow manually) ships binaries to testers:

| Platform | Destination |
| --- | --- |
| Android | Play **internal** track, `release_status: completed` (not draft) |
| iOS | **TestFlight** (waits for processing; internal testers) |

PR CI (`.github/workflows/android.yml`, `ios.yml`) builds and tests only — it does not publish.

## Versioning

| Platform | Scheme |
| --- | --- |
| Android | `versionCode = GITHUB_RUN_NUMBER`, `versionName = 1.0.$GITHUB_RUN_NUMBER` |
| iOS | Build / marketing patch from `GITHUB_RUN_NUMBER` via `APP_BUILD_NUMBER` |

## Required GitHub secrets

### Android

| Secret | Purpose |
| --- | --- |
| `KEYSTORE_FILE_BASE64` | Base64-encoded upload keystore (single line) |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |
| `PLAY_STORE_CONFIG_JSON` | Play Console service-account JSON |

### iOS

| Secret | Purpose |
| --- | --- |
| `MATCH_GIT_URL` | Private match certificates repo URL |
| `MATCH_PASSWORD` | Match encryption password |
| `MATCH_GIT_BASIC_AUTHORIZATION` | Optional; base64 `user:token` if the match repo needs auth |
| `APP_STORE_CONNECT_API_KEY_KEY_ID` | ASC API key id |
| `APP_STORE_CONNECT_API_KEY_ISSUER_ID` | ASC issuer id |
| `APP_STORE_CONNECT_API_KEY_KEY` | ASC `.p8` key content, base64-encoded |

## One-time store setup

1. **Play** — App id `com.thevinesh.dejavu`, finish required declarations, add internal testers, grant the service account access to the app (including release to testing tracks). For Fastlane listing sync to commit edits, the SA also needs permission to manage store presence.
2. **App Store Connect** — App for bundle id `com.thevinesh.dejavu`, TestFlight internal testing, match App Store certs/profiles for team `9S95C26JN6`.
3. Add the secrets above to this GitHub repo.

## Workflows

- **Deploy** (`.github/workflows/deploy.yml`) — shared JVM tests → signed AAB → Play internal; signed IPA → TestFlight.
- Fastlane lanes used by Deploy:
  - `androidApp`: `deploy` (AAB upload; metadata/images skipped)
  - `iosApp`: `build_release_artifact`, then `deploy` (TestFlight)

## Store listing sync (manual)

Listing copy, graphics, and screenshots live under Fastlane metadata paths and can be pushed without a new binary:

```bash
# App Store Connect (needs ASC API key env vars — same secrets as Deploy)
cd iosApp
bundle exec fastlane ios sync_store_listing

# Play Console (needs ANDROID_PLAY_CONFIG_JSON_PATH pointing at the SA JSON)
cd androidApp
export ANDROID_PLAY_CONFIG_JSON_PATH=/path/to/play_config.json
bundle exec fastlane android sync_store_listing
```

| Path | Contents |
| --- | --- |
| `iosApp/fastlane/metadata/` | ASC text, categories, review contact |
| `iosApp/fastlane/screenshots/` | Device-sized screenshots for `deliver` |
| `androidApp/fastlane/metadata/android/` | Play listings, icon, feature graphic, screenshots |

Maestro flow for capturing UI screenshots: `maestro/store_screenshots.yaml`.
