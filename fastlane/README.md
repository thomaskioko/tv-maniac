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

### ios snapshot_tests

```sh
[bundle exec] fastlane ios snapshot_tests
```

Run Snapshot Tests

### ios build_tvmaniac

```sh
[bundle exec] fastlane ios build_tvmaniac
```

Build iOS App (Debug, simulator)

### ios certs

```sh
[bundle exec] fastlane ios certs
```

Download signing certificates

### ios build_beta

```sh
[bundle exec] fastlane ios build_beta
```

Build beta and upload to TestFlight

### ios build_release

```sh
[bundle exec] fastlane ios build_release
```

Build production release and upload to TestFlight

### ios deploy_app_store

```sh
[bundle exec] fastlane ios deploy_app_store
```

Submit to App Store review

### ios build_tvmaniac_ipa

```sh
[bundle exec] fastlane ios build_tvmaniac_ipa
```

Build TvManiac IPA (Debug, simulator)

### ios lint

```sh
[bundle exec] fastlane ios lint
```

Run SwiftLint

### ios format_swift_code

```sh
[bundle exec] fastlane ios format_swift_code
```

Format Swift code with SwiftFormat

### ios check_swift_format

```sh
[bundle exec] fastlane ios check_swift_format
```

Check Swift code formatting with SwiftFormat

----


## Android

### android deploy_play_store

```sh
[bundle exec] fastlane android deploy_play_store
```

Deploy AAB to a Play Store track (default: internal)

### android deploy_production

```sh
[bundle exec] fastlane android deploy_production
```

Deploy AAB to Play Store production track

### android distribute_firebase

```sh
[bundle exec] fastlane android distribute_firebase
```

Distribute release APK via Firebase App Distribution

### android promote

```sh
[bundle exec] fastlane android promote
```

Promote a release between tracks

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
