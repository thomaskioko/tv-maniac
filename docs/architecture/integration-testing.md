# Integration Testing

## Table of Contents

- [Module Layout](#module-layout)
- [Test Harness](#test-harness)
- [Robot Pattern](#robot-pattern)
- [Network Stubbing](#scenarios-and-network-stubbing)
- [Running Tests](#running-integration-tests)

Integration tests run the full Android UI stack under Robolectric using a real Metro dependency graph with test overrides (fake auth, mock Ktor engines, test dispatchers).

## Module Layout

- **`core/integration/infra`**: DI overrides and fakes. KMP-based. Contains `TestTraktAuthManagerBindingContainer` under `bindings/`.
- **`core/integration/ui`**: UI scaffolding. Contains `BaseInstrumentationComposeTest` under `ui/`; `BaseRobot`; and `ComposeDsl.kt` (which includes UiAutomator helpers like `SystemDialog`).
- **`:app`**: Glue layer. Contains `TvManiacTestApplication`, `TvManiacTestActivity`, `BaseAppFlowTest`, flow tests under `compose/flows/`, journey tests under `compose/journey/`, and robots under `compose/robot/`.

## Test Harness

- **`TvManiacTestApplication`**: Builds test graph with overrides applied from `core/integration/infra`.
- **`TvManiacTestActivity`**: Renders `RootScreen` inside test graph. See [TestActivity Wiring](#testactivity-wiring).
- **`BaseInstrumentationComposeTest`**: Abstract base in `core/integration/ui`. Annotated `@RunWith(AndroidJUnit4::class)`. Provides `composeTestRule`, synthetic Decompose `ComponentContext`, and `MockEngineResetRule` to clear stubs between tests.
- **`BaseAppFlowTest`**: App-level abstract subclass of `BaseInstrumentationComposeTest`. Resets auth in `onBeforeTest`, and exposes lazy robots and `Scenarios`. Annotated `@Config(sdk = [33], application = TvManiacTestApplication::class)` so Robolectric knows which `Application` subclass to instantiate; instrumentation runner ignores that annotation and uses `TvManiacInstrumentationRunner` instead.

## TestActivity Wiring

`TvManiacTestActivity` renders `RootScreen` inside `CompositionLocalProvider(LocalAutoAdvanceEnabled provides false)` block. `LocalAutoAdvanceEnabled` defined in `features/discover/ui/src/main/java/com/thomaskioko/tvmaniac/discover/ui/component/AutoAdvanceLocal.kt` defaults to `true` in production. Setting it to `false` in test activity disables Discover featured pager auto-advance `LaunchedEffect` so pager assertions remain deterministic regardless of test wall-time.

Future test-only `CompositionLocal` overrides follow this pattern: declare `ProvidableCompositionLocal` in production feature module with production default, then override only in `TvManiacTestActivity`.

## Robot Pattern

Each screen has robot wrapping `ComposeContentTestRule`. Robots express intent without calling repositories or DAOs directly.

```kotlin
internal class DiscoverRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {
    fun verifyDiscoverScreenIsShown() = verifyTagShown(DiscoverTestTags.SCREEN_TEST_TAG)
    fun clickShowCard(id: Long) = click(DiscoverTestTags.showCard(id))
}
```

Assertions target UI state visible to robot. Do not add capture infrastructure for inspecting what was sent to fakes; assert behavioral signals (UI state or DAO state).

## Fakes Shipped With the Harness

Three fakes central to flow and journey tests.

**`FakeTraktAuthManager`** (`data/traktauth/testing`): bound at `AppScope`. Test-only setter `setOnLaunchWebView { ... }` configures what happens when presenter calls `traktAuthManager.launchWebView()`. Production `AndroidTraktAuthManager.launchWebView()` opens Custom Tab and later returns tokens; fake invokes configured callback instead. Typical use:

```kotlin
graph.traktAuthManager.setOnLaunchWebView {
    scenarios.profile.stubProfileSyncEndpoints()
    scenarios.auth.stubLoggedInUser()
}
```

**`FakeDatastoreRepository`** (`data/datastore/testing`): bound at `AppScope`. Non-suspending setter `setNotificationPermissionAskedNow(asked: Boolean)` seeds permission-asked state before activity launches. Suspend interface counterpart `setNotificationPermissionAsked(asked)` deadlocks under `runBlocking` when `IntegrationTestDispatcherBindings` binds every dispatcher role to `Dispatchers.Main` (a `TestDispatcher`). Always use synchronous setter from `@Before`.

**`TestTraktAuthManagerBindingContainer`** (`core/integration/infra/.../bindings/TestTraktAuthManagerBindingContainer.kt`): `@BindingContainer` contributed to `ActivityScope` replaces `AndroidTraktAuthManager` and routes binding to `FakeTraktAuthManager` `AppScope` instance. Without this container `ActivityScope` production binding shadows `AppScope` fake and presenters resolve real Android implementation, which throws because `registerResult()` was never called.

## Network Stubbing

`Scenarios` groups stubs by feature area. `MockEngineHandler` registers responses:

- **`stubFixture(path)`**: Loads JSON from `core/integration/infra/src/androidMain/resources/fixtures/` via `ClassLoader.getResourceAsStream`. Path relative to that root.
- **`stub(path, body)`**: Inline JSON with specified HTTP status.
- **`stubEndpoint(endpoint)`**: Reworked helper that picks success/error fixture based on status code.

`stubByQuery` allows varying responses based on query parameters. Stubs cleared between tests by `MockEngineResetRule` on a single shared handler.

## System Dialog Helper

`dismissSystemDialog(SystemDialog.NotificationPermissionDeny)` in `core/integration/ui/.../ComposeDsl.kt` dismisses system `POST_NOTIFICATIONS` permission dialog via UiAutomator. Behaviour by runner:

- Robolectric (`Build.FINGERPRINT` starts with `"robolectric"`): no system dialog appears. No-op.
- Pre-Tiramisu (API < 33): `LaunchedEffect` routes directly to `onNotificationPermissionResult(true)`. No-op.
- Instrumentation on API 33+: locates platform deny button by resource id (`com.android.permissioncontroller:id/permission_deny_button`) with text fallbacks (`"Don't allow"`, `"Deny"`), clicks it, then waits for window to disappear.

Call immediately after `rootRobot.acceptNotificationRationale()` whenever test exercises rationale Enable path. Requires `androidx.test.uiautomator:uiautomator` wired as `api(libs.androidx.uiautomator)` in `core/integration/ui` `androidMain`.

## pressBack Behaviour

`pressBack()` in `core/integration/ui/.../ComposeDsl.kt` dispatches via `activity.onBackPressedDispatcher.onBackPressed()`. Material 3 `Dialog` and `ModalBottomSheet` windows not registered with activity dispatcher, so back press bypasses dialog and pops underlying navigation stack. To dismiss dialog or modal sheet, click dialog's confirm or dismiss button, or trigger action that closes it (for example `episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)` for episode sheet).

Helper falls back from `Stage.RESUMED` to `Stage.PAUSED` because real Android briefly transitions through `PAUSED` while modal dismissing. Robolectric stays at `RESUMED` so fallback is no-op there.

## Test Folder Layout

- `app/src/sharedTest/.../compose/flows/`: per-feature flow tests (one class per surface: discover, search, calendar, settings, etc.). Each class covers one surface in isolation.
- `app/src/sharedTest/.../compose/journey/`: end-to-end journey tests (`UnauthenticatedUserJourneyTest`, `AuthenticatedUserJourneyTest`). Each journey walks one realistic user lifecycle across multiple surfaces.

See [journey-tests.md](journey-tests.md) for journey test pattern and existing journeys.

## Writing Flow Test

1. Add flow test class to `compose/flows/` extending `BaseAppFlowTest`.
2. Stub endpoints in `@Before` via `scenarios`.
3. Use robots to drive and assert UI. One robot per screen.
4. Assert on UI state only. Do not call repositories or DAOs in test body.

For full-lifecycle scenarios crossing multiple surfaces, write journey test instead. See [journey-tests.md](journey-tests.md).

## Running Tests

```bash
# Primary local loop. Fast. Runs under Robolectric on JVM.
./gradlew :app:testDebugUnitTest

# Gradle Managed Device. Headless. Provisions Pixel 6 API 34 ATD emulator automatically.
./gradlew :app:pixel6Api34DebugAndroidTest

# Connected test against booted emulator on adb. Use API 34 or 35 only.
# Espresso 3.6.x cannot initialize on API 36 because InputManager.getInstance was removed
# in Android 16. GMD path above unaffected.
./gradlew :app:connectedDebugAndroidTest
```
