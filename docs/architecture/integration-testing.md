# Integration Testing

## Table of Contents

- [Module Layout](#module-layout)
- [Testing Infrastructure](#testing-infrastructure)
- [Robot Pattern](#robot-pattern)
- [Network Stubbing](#scenarios-and-network-stubbing)
- [Running Tests](#running-integration-tests)

Integration tests run the full Android UI stack under Robolectric using a real Metro dependency graph
with test overrides (fake auth, mock Ktor engines, test dispatchers).

## Module Layout

- **`core/integration/infra`**: DI overrides and fakes. KMP-based. Contains
  `TestTraktAuthManagerBindingContainer` under `bindings/`.
- **`core/integration/ui`**: UI scaffolding. Contains `BaseRobot.kt` and `SystemDialogUtil.kt`.
- **`:app`**: Glue layer. Contains `TvManiacTestApplication`, `TvManiacTestActivity`, `BaseAppFlowTest`,
  flow tests under `compose/flows/`, journey tests under `compose/journey/`, and robots under
  `compose/robot/`.

## Testing Infrastructure

- **`TvManiacTestApplication`** (`app/src/sharedTest/.../TvManiacTestApplication.kt`): builds the
  test graph with `core/integration/infra` overrides; `resetAppComponent()` discards the cached
  graph between tests.
- **`TvManiacTestActivity`**: renders `RootScreen` inside the test graph. See
  [TestActivity Wiring](#testactivity-wiring).
- **`BaseAppFlowTest`** (`app/src/sharedTest/.../BaseAppFlowTest.kt`): abstract class annotated
  `@RunWith(AndroidJUnit4::class)` and
  `@Config(sdk = [33], application = TvManiacTestApplication::class)`. Exposes a single helper,
  `runAppFlowTest(block: AppFlowScope.() -> Unit)`.

When a test runs, `runAppFlowTest` resets the shared `MockEngineHandler`, calls
`application.resetAppComponent()`, then opens v2 `runAndroidComposeUiTest<TvManiacTestActivity>`
from `androidx.compose.ui.test.v2`. The latter installs a fresh `TestDispatcher` before launching
the activity. The lambda's `AppFlowScope` receiver hands you `composeUi`, `graph`, `activityGraph`,
a synthetic `componentContext`, lazy robots (`discoverRobot`, `showDetailsRobot`, etc.), and a
`scenarios` instance that shares the same handler and graph.

### Per-test graph reset

`AppCoroutineDispatchers` captures `Dispatchers.Main.immediate` at first graph access. Without
calling `resetAppComponent()` before each test, the second test's coroutines would be scheduled
on the first test's `TestDispatcher`, which is already stopped. The reset ensures the dispatcher
captured inside the graph belongs to the active test scheduler.

## TestActivity Wiring

`TvManiacTestActivity` renders `RootScreen` inside
`CompositionLocalProvider(LocalAutoAdvanceEnabled provides false)`. `LocalAutoAdvanceEnabled` is
defined in `features/discover/ui/.../AutoAdvanceLocal.kt` and defaults to `true` in production.
Setting it to `false` in the test activity disables the Discover featured pager auto-advance
`LaunchedEffect` so pager assertions remain deterministic regardless of test wall-time.

Future test-only `CompositionLocal` overrides follow this pattern: declare a `ProvidableCompositionLocal`
in the production feature module with the production default, then override it only in
`TvManiacTestActivity`.

## Robot Pattern

Each screen has a robot that wraps `ComposeUiTest` and extends `BaseRobot`. Robots express intent
without calling repositories or DAOs directly.

```kotlin
internal class DiscoverRobot(composeUi: ComposeUiTest) : BaseRobot(composeUi) {
    fun assertDiscoverScreenDisplayed() = assertDisplayed(DiscoverTestTags.SCREEN_TEST_TAG)
    fun clickShowCard(id: Long) = click(DiscoverTestTags.showCard(id))
}
```

`BaseRobot` (`core/integration/ui/.../BaseRobot.kt`) provides the complete primitive surface:
`click`, `assertDisplayed`, `assertExists`, `assertDoesNotExist`, `awaitTag`, `pressBack`,
`advanceTime`, `scrollTo`, `scrollToListTag`, `swipeLeft`, `swipeRight`, and more. Subclasses call
inherited methods directly; `composeUi` is only referenced when constructing sibling robots.

Assertions target UI state visible to the robot. Do not add capture infrastructure for inspecting
what was sent to fakes; assert behavioral signals (UI state or DAO state).

## Fakes Shipped With the Harness

Three fakes central to flow and journey tests.

**`FakeTraktAuthManager`** (`data/traktauth/testing`): bound at `AppScope`. Test-only setter
`setOnLaunchWebView { ... }` configures what happens when a presenter calls
`traktAuthManager.launchWebView()`. Production `AndroidTraktAuthManager.launchWebView()` opens a
Custom Tab and later returns tokens; the fake invokes the configured callback instead. Typical use:

```kotlin
graph.traktAuthManager.setOnLaunchWebView {
    scenarios.profile.stubProfileSyncEndpoints()
    scenarios.auth.stubLoggedInUser()
}
```

**`FakeDatastoreRepository`** (`data/datastore/testing`): bound at `AppScope`. Non-suspending setter
`setNotificationPermissionAskedNow(asked: Boolean)` seeds permission-asked state before the activity
launches. The suspend counterpart `setNotificationPermissionAsked(asked)` deadlocks under
`runBlocking` when `TestDispatcherBindingContainer` binds every dispatcher role to
`Dispatchers.Main` (a `TestDispatcher`). Always use the synchronous setter, called at the top of
`runAppFlowTest { ... }` before driving any robot.

**`TestTraktAuthManagerBindingContainer`**
(`core/integration/infra/.../bindings/TestTraktAuthManagerBindingContainer.kt`):
`@BindingContainer` contributed to `ActivityScope` that replaces `AndroidTraktAuthManager` and
routes the binding to the `FakeTraktAuthManager` `AppScope` instance. Without this container the
`ActivityScope` production binding shadows the `AppScope` fake and presenters resolve the real
Android implementation, which throws because `registerResult()` was never called.

## Network Stubbing

`Scenarios` groups stubs by feature area. `MockEngineHandler` registers responses:

- **`stubFixture(path)`**: Loads JSON from `core/integration/infra/src/androidMain/resources/fixtures/`
  via `ClassLoader.getResourceAsStream`. Path relative to that root.
- **`stub(path, body)`**: Inline JSON with specified HTTP status.
- **`stubEndpoint(endpoint)`**: Reworked helper that picks success/error fixture based on status code.

`stubByQuery` allows varying responses based on query parameters. Stubs are cleared between tests by
`MockEngineHandler.handler.reset()` inside `runAppFlowTest`, before the activity launches.

## System Dialog Helper

`dismissSystemDialog(SystemDialog.NotificationPermissionDeny)` in
`core/integration/ui/.../SystemDialogUtil.kt` dismisses the system `POST_NOTIFICATIONS` permission
dialog via UiAutomator. It is a top-level function with no receiver; call it bare inside
`runAppFlowTest { ... }`. Behaviour by runner:

- Robolectric (`Build.FINGERPRINT` starts with `"robolectric"`): no system dialog appears. No-op.
- Pre-Tiramisu (API < 33): `LaunchedEffect` routes directly to `onNotificationPermissionResult(true)`.
  No-op.
- Instrumentation on API 33+: locates the platform deny button by resource id
  (`com.android.permissioncontroller:id/permission_deny_button`) with text fallbacks
  (`"Don't allow"`, `"Deny"`), clicks it, then waits for the window to disappear.

Call immediately after `rootRobot.acceptNotificationRationale()` whenever a test exercises the
rationale Enable path. Requires `androidx.test.uiautomator:uiautomator` wired as
`api(libs.androidx.uiautomator)` in `core/integration/ui` `androidMain`.

## pressBack Behaviour

`pressBack()` is a method on `BaseRobot` (`core/integration/ui/.../BaseRobot.kt`). It dispatches via
`activity.onBackPressedDispatcher.onBackPressed()`. Material 3 `Dialog` and `ModalBottomSheet`
windows are not registered with the activity dispatcher, so a back press bypasses the dialog and
pops the underlying navigation stack. To dismiss a dialog or modal sheet, click its confirm or
dismiss button, or trigger an action that closes it (for example
`episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)` for the episode sheet).

The helper falls back from `Stage.RESUMED` to `Stage.PAUSED` because real Android briefly
transitions through `PAUSED` while a modal is dismissing. Robolectric stays at `RESUMED`, so the
fallback is a no-op there.

## Test Folder Layout

- `app/src/sharedTest/.../compose/flows/`: per-feature flow tests (one class per surface: discover,
  search, calendar, settings, etc.). Each class covers one surface in isolation.
- `app/src/sharedTest/.../compose/journey/`: end-to-end journey tests
  (`UnauthenticatedUserJourneyTest`, `AuthenticatedUserJourneyTest`). Each journey walks one
  realistic user lifecycle across multiple surfaces.

See [journey-tests.md](journey-tests.md) for journey test pattern and existing journeys.

## Writing Flow Test

1. Add a flow test class to `compose/flows/` extending `BaseAppFlowTest`.
2. Each `@Test` method is a single expression wrapping its body in `runAppFlowTest { ... }`.
3. At the top of the lambda, call `scenarios.X()` to register stubs before driving any robot.
4. Use robots to drive and assert UI. One robot per screen.
5. Assert on UI state only. Do not call repositories or DAOs in the test body. No `@Before`.

```kotlin
internal class DiscoverToShowDetailsFollowFlowTest : BaseAppFlowTest() {

    @Test
    fun givenShow_whenTrackIsClicked_thenPersistsInFollowedShows() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.assertTrackButtonDisplayed()
        showDetailsRobot.clickTrackButton()
        showDetailsRobot.assertStopTrackingButtonDisplayed()
    }
}
```

For full-lifecycle scenarios crossing multiple surfaces, write a journey test instead. See
[journey-tests.md](journey-tests.md).

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
