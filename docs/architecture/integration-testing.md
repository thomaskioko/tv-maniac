# Integration Testing

## Table of Contents

- [Module Layout](#module-layout)
- [Testing Infrastructure](#testing-infrastructure)
- [Fakes Shipped With the Harness](#fakes-shipped-with-the-harness)
- [Network Stubbing](#network-stubbing)
- [Test Folder Layout](#test-folder-layout)
- [Running Tests](#running-tests)

Integration tests run the full Android UI stack under [Robolectric](glossary.md#robolectric) using a real [Metro](glossary.md#metro) dependency graph with test overrides such as a fake auth manager, mock Ktor engines, and test dispatchers.

For the patterns used to write new tests, see [`flow-test-patterns.md`](flow-test-patterns.md). For multi-surface lifecycle tests, see [`journey-tests.md`](journey-tests.md).

## Module Layout

- `core/integration/infra`: dependency injection overrides and shared fakes. Kotlin Multiplatform module. Contains `TestTraktAuthManagerBindingContainer` under `bindings/`.
- `core/integration/ui`: UI scaffolding. Contains `BaseRobot.kt` and `SystemDialogUtil.kt`.
- `:app`: the glue layer. Contains `TvManiacTestApplication`, `TvManiacTestActivity`, `BaseAppFlowTest`, flow tests under `compose/flows/`, journey tests under `compose/journey/`, and robots under `compose/robot/`.

## Testing Infrastructure

- `TvManiacTestApplication` (`app/src/sharedTest/.../TvManiacTestApplication.kt`): builds the test graph with `core/integration/infra` overrides. `resetAppComponent()` discards the cached graph between tests.
- `TvManiacTestActivity`: renders `RootScreen` inside the test graph. Wraps the content in `CompositionLocalProvider(LocalAutoAdvanceEnabled provides false)` so the Discover featured pager auto-advance `LaunchedEffect` is disabled during tests, keeping pager assertions deterministic.
- `BaseAppFlowTest` (`app/src/sharedTest/.../BaseAppFlowTest.kt`): abstract class annotated `@RunWith(AndroidJUnit4::class)` and `@Config(sdk = [33], application = TvManiacTestApplication::class)`. Exposes one helper, `runAppFlowTest(block: AppFlowScope.() -> Unit)`.

When a test runs, `runAppFlowTest` resets the shared `MockEngineHandler`, calls `application.resetAppComponent()`, and then opens v2 `runAndroidComposeUiTest<TvManiacTestActivity>` from `androidx.compose.ui.test.v2`. The latter installs a fresh `TestDispatcher` before launching the activity. The lambda's `AppFlowScope` receiver hands the test `composeUi`, `graph`, `activityGraph`, a synthetic `componentContext`, lazy robots (`discoverRobot`, `showDetailsRobot`, and others), and a `scenarios` instance that shares the same handler and graph.

### Per-test graph reset

`AppCoroutineDispatchers` captures `Dispatchers.Main.immediate` at first graph access. Without `resetAppComponent()` between tests, the second test's coroutines would be scheduled on the first test's `TestDispatcher`, which is already stopped. The reset ensures the dispatcher captured inside the graph belongs to the active test scheduler.

### Test-only `CompositionLocal` overrides

To override a feature flag in tests, declare a `ProvidableCompositionLocal` in the production feature module with the production default. Override it only in `TvManiacTestActivity`. `LocalAutoAdvanceEnabled` is the existing example.

## Fakes Shipped With the Harness

Three fakes are central to flow and journey tests.

### `FakeTraktAuthManager`

Located in `data/traktauth/testing` and bound at [`AppScope`](glossary.md#appscope). The test-only setter `setOnLaunchWebView { ... }` configures what happens when a presenter calls `traktAuthManager.launchWebView()`. Production opens a Custom Tab; the fake invokes the configured callback instead.

```kotlin
graph.traktAuthManager.setOnLaunchWebView {
    scenarios.profile.stubProfileSyncEndpoints()
    scenarios.auth.stubLoggedInUser()
}
```

### `FakeDatastoreRepository`

Located in `data/datastore/testing` and bound at `AppScope`. The non-suspending setter `setNotificationPermissionAskedNow(asked: Boolean)` seeds permission state before the activity launches. The suspend counterpart `setNotificationPermissionAsked(asked)` deadlocks under `runBlocking` when `TestDispatcherBindingContainer` binds every dispatcher role to `Dispatchers.Main`. Always use the synchronous setter at the top of `runAppFlowTest { ... }` before driving any robot.

### `TestTraktAuthManagerBindingContainer`

Located at `core/integration/infra/.../bindings/`. Contributed to [`ActivityScope`](glossary.md#activityscope) so it overrides the production `AndroidTraktAuthManager` and routes the binding to the `FakeTraktAuthManager` `AppScope` instance. Without this container, the `ActivityScope` production binding shadows the `AppScope` fake and presenters resolve the real Android implementation.

## Network Stubbing

`Scenarios` groups stubs by feature area. `MockEngineHandler` registers responses through three helpers.

- `stubFixture(path)`: loads JSON from `core/integration/infra/src/androidMain/resources/fixtures/` through `ClassLoader.getResourceAsStream`. Path is relative to that root.
- `stub(path, body)`: inline JSON with a specified HTTP status.
- `stubEndpoint(endpoint)`: picks a success or error fixture based on status code.

`stubByQuery` returns different responses based on query parameters. Stubs are cleared between tests by `MockEngineHandler.handler.reset()` inside `runAppFlowTest`, before the activity launches.

## Test Folder Layout

- `app/src/sharedTest/.../compose/flows/`: feature flow tests, one class for each surface (discover, search, calendar, settings, and others). Each class covers one surface in isolation.
- `app/src/sharedTest/.../compose/journey/`: end-to-end journey tests (`UnauthenticatedUserJourneyTest`, `AuthenticatedUserJourneyTest`). Each journey walks one realistic user lifecycle across multiple surfaces.

## Running Tests

```bash
# Primary local loop. Fast. Runs under Robolectric on the JVM.
./gradlew :app:testDebugUnitTest

# Gradle Managed Device. Headless. Provisions a Pixel 6 API 34 emulator automatically.
./gradlew :app:pixel6Api34DebugAndroidTest

# Connected test against a booted emulator on adb. Use API 34 or 35 only.
# Espresso 3.6.x cannot initialize on API 36 because InputManager.getInstance was
# removed in Android 16. The Gradle Managed Device path above is unaffected.
./gradlew :app:connectedDebugAndroidTest
```
