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

When a test runs, `runAppFlowTest` resets the shared `MockEngineHandler`, calls `application.resetAppComponent()`, creates one `StandardTestDispatcher`, installs it with `Dispatchers.setMain`, and passes it as the `effectContext` of v2 `runAndroidComposeUiTest<TvManiacTestActivity>` from `androidx.compose.ui.test.v2`. The lambda's `AppFlowScope` receiver hands the test `composeUi`, `graph`, `activityGraph`, a synthetic `componentContext`, lazy robots (`discoverRobot`, `showDetailsRobot`, and others), and a `scenarios` instance that shares the same handler, graph, and `composeUi`.

### One scheduler for the whole test

`runAndroidComposeUiTest` does not install anything as `Dispatchers.Main`. Read from
`androidx.compose.ui:ui-test-android:1.12.0`: `setMain` appears nowhere in the artifact, composition
is given a private `StandardTestDispatcher`, and the `runTest` body is given a second one whose
`TestCoroutineScheduler` the library removes from the shared context on purpose. `MainTestClockImpl`
advances only the composition scheduler, and nothing bridges it to the clock Robolectric uses for its
main looper.

That matters because presenters never read the injected dispatchers. `LifecycleOwner.coroutineScope`
defaults to `Dispatchers.Main.immediate`, and `asValue` names it twice more. Left alone, every
presenter, store, and data access object runs on the Handler backed main looper while recomposition
runs on the composition scheduler, and `waitUntil` bounds both against the wall clock. Work parked
behind a `delay` in app code then never resumes at all: no `debounce`, no `WhileSubscribed` stop
timeout, no retry backoff.

`Dispatchers.setMain(testDispatcher)` is what puts them on one timeline, because it is the only way
to redirect an expression the production code hardcodes. Passing the same dispatcher as
`effectContext` makes composition and `MainTestClock` share that scheduler too. `resetAppComponent()`
still runs between tests so the graph does not hold a dispatcher belonging to a finished test.

### Scenario setup must never block the test thread

Every dispatcher role resolves to the scheduler the test thread advances, so a `runBlocking` in setup
deadlocks: it holds the one thread that could complete the work it is waiting on. `Scenarios.runSetup`
starts the work undispatched, which completes it inline when nothing actually suspends, and waits for
idleness only when it does. Prefer a synchronous setter on a fake over a suspending one.

Two ordering rules follow, both enforced by `stubActiveProvider`. A stub registered later wins for a
given path, so an override registers after the catalog set; pass it as the `overrides` block. Sign-in
comes last, because it starts a sync that must find every response already registered.

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
