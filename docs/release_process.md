# Mobile release process (DejaVu)

Push to `main` (or manual `Deploy` workflow) ships binaries straight to testers:

- **Android** — signed AAB uploaded to Play **internal** track with `release_status: completed` (not draft), so internal testers get it immediately.
- **iOS** — signed IPA uploaded to **TestFlight**; the job waits for processing so the build is ready for internal testers.

PR CI (`.github/workflows/android.yml` / `ios.yml`) only builds and tests; it does not publish.

## Required GitHub secrets

### Android

| Secret | Purpose |
| --- | --- |
| `KEYSTORE_FILE_BASE64` | Base64-encoded upload keystore (single line) |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |
| `PLAY_STORE_CONFIG_JSON` | Raw Play Console service-account JSON |

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

1. Create Play Console app `com.thevinesh.dejavu`, complete required declarations, add internal testers, and grant the service account access.
2. Create App Store Connect app `com.thevinesh.dejavu`, enable TestFlight internal testing, and add match App Store certs/profiles for that bundle id (team `9S95C26JN6`).
3. Add the secrets above to this GitHub repo.

## Versioning

- Android: `versionCode = GITHUB_RUN_NUMBER`, `versionName = 1.0.$GITHUB_RUN_NUMBER`
- iOS: build number / marketing patch from `GITHUB_RUN_NUMBER` via `APP_BUILD_NUMBER`
