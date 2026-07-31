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

## Screenshots

`./scripts/store_screenshots.sh all` captures every form factor with
`maestro/store_screenshots.yaml` and copies the results into the Fastlane paths
above. Individual steps are `capture-ios`, `capture-android`, and `stage`.

Raw captures are kept under `store_assets/screenshots/` and staged copies are
committed, so a listing sync needs no devices.

Both stores reject off-spec pixel dimensions, so each size is captured natively
rather than resized afterwards — `stage` re-checks dimensions and fails before
upload if a capture is wrong.

| Store | Form factor | Captured on | Size |
| --- | --- | --- | --- |
| App Store | iPhone 6.9" | iPhone 17 Pro Max sim | 1320x2868 |
| App Store | iPad 13" | iPad Pro 13-inch (M4) sim | 2064x2752 |
| Play | Phone | emulator at `wm size 1080x1920` | 1080x1920 |
| Play | 7-inch tablet | emulator at `wm size 1200x1920` | 1200x1920 |
| Play | 10-inch tablet | emulator at `wm size 1600x2560` | 1600x2560 |

Two constraints drive those choices:

- **App Store Connect only requires the largest class per device family** and
  scales the rest down. Supplying anything smaller for iPhone (for example
  1206x2622, which is the 6.3" class) leaves the 6.9" slot empty, and review
  then reports `You must upload a screenshot for 6.5-inch iPhone displays`.
- **Play caps the long side at twice the short side**, so a stock 20:9 phone
  capture is rejected. The emulator display is driven to Play's recommended
  sizes instead of cropping or letterboxing afterwards.

`deliver` picks the display class from the pixel dimensions, not the file name.
The `APP_IPHONE_67_*` / `APP_IPAD_PRO_3GEN_129_*` prefixes are `deliver`'s own
display-type constants, which still carry the older 6.7"/12.9" labels for what
Apple now calls the 6.9" and 13" classes.

### Known sync failures

| Symptom | Cause | Handling |
| --- | --- | --- |
| Play: `Google Api Error: Invalid request - Upload has already been terminated` | The Google API client retries a resumable upload session that Google already closed; the retry returns a non-retryable 400. `supply` aborts on the first API error unless retries are enabled. | `sync_store_listing` sets `SUPPLY_UPLOAD_MAX_RETRIES=5`, which restarts the upload from the file path, and keeps `timeout` low so a stalled connection fails fast enough for the retry to help. |
| App Store: two copies of every screenshot | ASC does not list a screenshot until processing finishes, so `deliver`'s post-upload check sees an empty set, assumes the batch failed, and uploads it again. | `sync_store_listing` finishes with a prune pass that drops duplicate checksums. Run `fastlane ios prune_screenshots` to clean up without re-uploading. |
| Play: icon or feature graphic never changes | `supply` reads these as `images/icon.png` and `images/featureGraphic.png`. Nested in `images/icon/icon.png` they are skipped with no upload and no error. | Keep them flat; `scripts/store_screenshots.sh stage` writes them to the correct paths. |

`sync_image_upload` compares checksums against the live listing, so only changed
assets are uploaded. The first sync after a screenshot change does the work; runs
after that upload nothing, which keeps the flaky-upload exposure small.
