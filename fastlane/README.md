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

Build iOS App

### ios build_tvmaniac_ipa

```sh
[bundle exec] fastlane ios build_tvmaniac_ipa
```

Build TvManiac IPA

### ios clear_derived_data_lane

```sh
[bundle exec] fastlane ios clear_derived_data_lane
```

Clear derived data

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

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
