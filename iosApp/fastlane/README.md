fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## iOS

### ios build_release_artifact

```sh
[bundle exec] fastlane ios build_release_artifact
```

Build the TestFlight-ready iOS release artifact

### ios deploy

```sh
[bundle exec] fastlane ios deploy
```

Upload a prebuilt iOS release artifact to TestFlight

### ios sync_store_listing

```sh
[bundle exec] fastlane ios sync_store_listing
```

Sync App Store listing metadata and screenshots (no binary upload)

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
