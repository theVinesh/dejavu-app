# DejaVu Release Process

Releases are tag-driven. Creating a release tag promotes the newest internal build to
production on both platforms and submits iOS for App Store review, with release notes
generated automatically from what changed.

## The release ritual

1. Make sure `main` is green. The tag script refuses to run otherwise
   (it checks all GitHub check-runs on HEAD; override with `--skip-ci-check` in emergencies).
2. From a clean, up-to-date `main`, run:

   ```bash
   scripts/release-tag.sh --dry-run   # preview the tag + changelog (no mutation)
   scripts/release-tag.sh             # create + push the annotated tag
   ```

   The script derives the next `vX.Y.Z` from conventional commits since the last release tag:
   - `BREAKING CHANGE:` (commit body) or `type!: ...` -> major
   - `feat:` / `feat(scope):` -> minor
   - any other conventional prefix (`fix:`, `chore:`, ...) or unknown -> patch
   - nothing release-worthy since the last tag -> the script exits without tagging

3. The pushed tag fires the `Release to Production` GitHub Actions workflow:
   - **Android**: promotes the newest Play internal release to production and attaches
     the generated "what's new" as the per-versionCode changelog.
   - **iOS**: finds the newest VALID TestFlight build, syncs the listing metadata and
     screenshots, writes the generated release notes, and submits for App Store review.
   - Both jobs log the exact versionCode / build number they acted on.

## What's new generation

The annotated tag message carries a changelog of every commit since the last tag.
The release workflow reads it back, strips conventional prefixes, truncates to store
limits (Play 500 chars, App Store 4000), and injects it into the listings. Empty
changelogs fall back to "Bug fixes and improvements." Generated in CI only; nothing
is committed back to the repo.

## Manual safety rails

- `workflow_dispatch` with `android_only: true` runs the Android promote without the
  iOS review submission (use for the first release of a new setup).
- The tag script has a race guard: if the computed tag exists (concurrent release),
  it refuses and asks you to fetch and re-run.
- Emergency override: `scripts/release-tag.sh --skip-ci-check`.

## Rollback notes

- **Play production promote is irreversible as an undo.** Rolling back means shipping
  the previous release as a newer release (standard Play behavior).
- **App Store submissions are withdrawable** while in review, from App Store Connect.

## Versioning convention

- Tag = user-facing release version (`vX.Y.Z`).
- The binary's internal versionCode/name is CI-run-number based and is never the tag.
  The release always acts on the newest internal build, so the tag does not need to
  encode a build number.

## Testing the script

`bash scripts/release-tag.test.sh` — unit tests against throwaway git repos
(baseline, feat/breaking/unknown bumps, tag filtering, guard rails). No stores, no builds.
