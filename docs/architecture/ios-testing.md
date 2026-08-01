# iOS Testing

## Table of Contents

- [Test Layers](#test-layers)
- [Kotlin/Native Tests](#kotlinnative-tests)
- [State Isolation](#state-isolation)
- [Snapshot Tests](#snapshot-tests)
- [UI Tests](#ui-tests)
- [Launch Environment](#launch-environment)
- [Running Tests](#running-tests)

Android's harness is described in [`integration-testing.md`](integration-testing.md).

## Test Layers

| Layer | Runs | Covers | CI job |
|---|---|---|---|
| Kotlin/Native tests | Simulator, no app | iOS `actual` declarations, [Metro](glossary.md#metro) graph construction, presenter logic on Kotlin/Native | `ios-test` |
| Snapshot tests | Simulator, no app | Rendering of stateless `XScreen` structs | `ios-snapshot-test` |
| UI tests | Simulator, real app | App launch, graph construction at runtime, framework linking, navigation | `ios-ui-test` |

`fastlane build_tvmaniac` compiles and links the app without launching it, and the snapshot targets never import `TvManiac`. The UI tests are the only layer that starts the binary, so they are the only layer that fails when `AppDelegate.init` throws.

## Kotlin/Native Tests

`./gradlew iosTest` runs every `iosTest` source set on `iosSimulatorArm64`. `HomePresenterIosTest` and `DefaultRootPresenterIosTest` build presenters from a real Metro graph; the rest are unit tests.

That graph is `TestGraph` (`core/integration/infra/src/iosMain/.../TestGraph.kt`), entered through `runTestWithGraph`. `core/integration/infra/src/jvmMain/.../TestGraph.kt` declares the same graph for the JVM. Metro materializes `@DependencyGraph.Factory` per target, so the two cannot share a declaration.

Pass `--continue`. Gradle otherwise stops at the first failing module and reports no others.

## State Isolation

`iosTest` runs one Kotlin/Native binary per module, so every test in a module shares one process and one set of files on disk. `FakeIosPlatformBindingContainer` replaces the three bindings that carry values from one test into the next, matching `TestJvmPlatformBindingContainer` on the JVM:

- **Keychain**: replaces `IosAuthStore` with `FakeAuthStore`. A Kotlin/Native test binary is not an app bundle and holds no keychain entitlement, so `KeychainSettings` fails every read with `-25291`.
- **Preferences**: replaces `DataStorePlatformBindingContainer`, which writes one file in `NSDocumentDirectory`. `createDataStore` in `DataStoreHelper.kt` also caches a single instance for the process, so building a new Metro graph returns the same `DataStore`. The override calls `PreferenceDataStoreFactory.createWithPath` against a unique temporary directory.
- **Database**: replaces `DatabasePlatformBindingContainer`, which opens a named file, with `createNativeSqliteDriver(inMemory = true)`.

A missing override surfaces as an assertion reading a value an earlier test wrote, such as `fontSizePercent=120` in a theme assertion. The failure depends on execution order. Add an override here for every new binding that writes to disk.

## Snapshot Tests

The `Snapshots` scheme runs them, and `ios/Packages/SnapshotTestingLib` holds the harness. Each feature package owns a `Tests/` target that renders its `XScreen` struct in light and dark against committed PNG baselines.

`XScreen` structs do not import `TvManiac`. That keeps the snapshot targets off the framework and holds the split: `XView` owns the presenter, `XScreen` takes a `State` struct and closures.

## UI Tests

`ios/tvmaniacUITests` drives the app through XCUITest.

`XCUIApplication.launchTvManiac(scenario:)` starts the app with the [launch environment](#launch-environment) below. `IosHttpEngineBindingContainer` (`ios-framework/src/iosMain/`) reads `TVMANIAC_STUB_SCENARIO` and returns either a `MockEngine` backed by `MockEngineHandler` or `Darwin.create()`. Metro resolves `replaces` at compile time, so both branches live in that one container. A launch without the variable reaches the live services.

`IosTestHooks` performs the two steps Android's journey tests run inside the test process:

- `clearPersistentStateIfNeeded()` deletes the database, the preferences file and the saved credentials. `AppDelegate.init` calls it before the first `appGraph` access, because the SQLDelight driver holds the database file open and `createDataStore` caches one instance for the process.
- `saveAuthStateIfNeeded(authStore:)` writes the credentials named by the scenario through the production `AuthStore`, which emits the login event `ContinueWatchingTasksInitializer` collects.

`AppLaunchTests` and `SettingsNavigationTests` assert that a screen container exists, which holds in every data state. `DiscoverContentTests` asserts on a show title that only the saved responses contain, and fails when the app reaches the live services instead.

Locate tabs by index through `app.tabBar.buttons.element(boundBy:)`. SwiftUI discards accessibility identifiers set inside `.tabItem`, so `HomeTestTags` constants do not resolve there.

`TestTags.swift` repeats the `core:test-tags` strings as literals. Importing `TvManiac` links the Kotlin/Native static framework into the test bundle, which fails on missing `_sqlite3_*` symbols unless the app's `OTHER_LDFLAGS` are copied.

## Launch Environment

| Variable | Effect |
|---|---|
| `TVMANIAC_STUB_SCENARIO` | Names an entry in the Kotlin `Scenarios` table. The app answers HTTP from that scenario's saved responses. |
| `TVMANIAC_FIXTURE_DIR` | Absolute path to the saved responses. `XCUIApplicationExtensions.swift` derives it from `#filePath`, so it resolves on a laptop and on CI without a build setting. |
| `TVMANIAC_CLEAR_STATE` | Set to `1` to delete the database, preferences and saved credentials before the graph is built. |

XCUITest runs in a separate process from the app, so the launch environment is the only channel between them.

## Running Tests

```bash
# Kotlin/Native
./gradlew iosTest -Papp.enableIos=true -Papp.debugOnly=false --continue

# Snapshots
bundle exec fastlane ios snapshot_tests

# UI tests
bundle exec fastlane ios ui_tests
```

Both Fastlane lanes build the KMP framework first. Set `TVMANIAC_SKIP_FRAMEWORK_BUILD=1` to reuse an existing build.

CI builds the app once, in `build-ios`, which runs `build_tvmaniac` and publishes the products. `ios-ui-test` downloads them and runs `fastlane ui_tests prebuilt:true`, which skips both the framework and the app build.

`TvManiacFramework/Package.swift` calls `fatalError` when `TvManiac.xcframework` is missing, so `xcodebuild -list` fails on a fresh checkout until `./scripts/build-kmp-framework.sh` has run.
