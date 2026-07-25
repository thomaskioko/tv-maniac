# iOS Testing

## Table of Contents

- [The Layers](#the-layers)
- [Kotlin/Native Tests](#kotlinnative-tests)
- [State Isolation](#state-isolation)
- [Snapshot Tests](#snapshot-tests)
- [Running Tests](#running-tests)

Android's equivalent harness is described in [`integration-testing.md`](integration-testing.md).

## The Layers

| Layer | Runs | Catches | CI job |
|---|---|---|---|
| Kotlin/Native tests | Simulator, no app | Broken iOS `actual` declarations, [Metro](glossary.md#metro) graph wiring, presenter logic on Kotlin/Native | `ios-test` |
| Snapshot tests | Simulator, no app | Visual regressions in stateless `XScreen` structs | `ios-snapshot-test` |

A third layer, XCUITest against the real app, is planned. Neither existing layer starts the binary, so a crash inside `AppDelegate.init` still reaches main: `fastlane build_tvmaniac` compiles and links the app without ever launching it, and snapshot tests never import `TvManiac`.

## Kotlin/Native Tests

`./gradlew iosTest` runs every `iosTest` source set on `iosSimulatorArm64`. Most are ordinary unit tests. Two of them, `HomePresenterIosTest` and `DefaultRootPresenterIosTest`, build presenters out of a real Metro graph and are the closest thing iOS has to Android's flow tests.

That graph is `TestGraph` in `core/integration/infra/src/iosMain/.../TestGraph.kt`, entered through `runTestWithGraph`. It is the iOS twin of the JVM `TestGraph`; the two are duplicated per source set because Metro materializes `@DependencyGraph.Factory` per target.

Always pass `--continue`. Without it Gradle stops at the first failing module and hides every other failure.

## State Isolation

`iosTest` runs one Kotlin/Native binary per module, so **all tests in a module share one process and one on-disk state**. `FakeIosPlatformBindingContainer` overrides the three pieces that would otherwise leak between tests, matching what `TestJvmPlatformBindingContainer` already does on the JVM:

- **Keychain**: `IosAuthStore` is replaced by `FakeAuthStore`. A Kotlin/Native test binary is not an app bundle, so it has no keychain entitlement and every keychain read fails with `-25291`.
- **Preferences**: `DataStorePlatformBindingContainer` writes a single file in `NSDocumentDirectory`, and `createDataStore` in `DataStoreHelper.kt` caches one instance for the whole process behind a lock. A fresh Metro graph does *not* get a fresh DataStore. The override builds `PreferenceDataStoreFactory` directly against a unique temporary directory, bypassing the singleton.
- **Database**: `DatabasePlatformBindingContainer` opens a named file. The override uses `createNativeSqliteDriver(inMemory = true)`.

When one of these is missing, the symptom is an assertion whose "actual" is a value some *other* test set, such as `fontSizePercent=120` appearing in a theme assertion. It reads as flakiness but is really execution order. Any new binding that persists state needs a matching override here.

## Snapshot Tests

Described by the `Snapshots` scheme plus `ios/Packages/SnapshotTestingLib`. Each feature package owns a `Tests/` target that renders its `XScreen` struct in light and dark against committed PNG baselines.

Screens stay free of `import TvManiac` on purpose. That keeps snapshot targets off the framework and keeps the View/Screen split honest: the `XView` owns the presenter, the `XScreen` is a plain `State` struct plus closures.

## Running Tests

```bash
# Kotlin/Native
./gradlew iosTest -Papp.enableIos=true -Papp.debugOnly=false --continue

# Snapshots
bundle exec fastlane ios snapshot_tests
```

The snapshot lane builds the KMP framework first. Set `TVMANIAC_SKIP_FRAMEWORK_BUILD=1` to reuse an existing build.

Note that `TvManiacFramework/Package.swift` calls `fatalError` when `TvManiac.xcframework` is missing, so a fresh checkout cannot even list Xcode schemes until `./scripts/build-kmp-framework.sh` has run.
