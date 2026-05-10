# Flow Test Patterns

## Table of Contents

- [Robot Pattern](#robot-pattern)
- [System Dialog Helper](#system-dialog-helper)
- [pressBack Behaviour](#pressback-behaviour)
- [Writing a Flow Test](#writing-a-flow-test)

A flow test exercises one feature surface in isolation through the integration test harness. The harness, fakes, and network stubbing helpers are documented in [`integration-testing.md`](integration-testing.md). This page covers the patterns used to write new flow tests.

## Robot Pattern

Each screen has a [robot](glossary.md#robot) that wraps `ComposeUiTest` and extends `BaseRobot`. Robots express intent without calling repositories or Data Access Objects directly.

```kotlin
internal class DiscoverRobot(composeUi: ComposeUiTest) : BaseRobot(composeUi) {
    fun assertDiscoverScreenDisplayed() = assertDisplayed(DiscoverTestTags.SCREEN_TEST_TAG)
    fun clickShowCard(id: Long) = click(DiscoverTestTags.showCard(id))
}
```

`BaseRobot` (`core/integration/ui/.../BaseRobot.kt`) provides the complete primitive surface: `click`, `assertDisplayed`, `assertExists`, `assertDoesNotExist`, `awaitTag`, `pressBack`, `advanceTime`, `scrollTo`, `scrollToListTag`, `swipeLeft`, `swipeRight`, and others. Subclasses call inherited methods directly. `composeUi` is referenced only when constructing sibling robots.

Assertions target UI state visible to the robot. Do not add capture infrastructure for inspecting what was sent to fakes. Assert behavioral signals: either UI state or Data Access Object state.

## System Dialog Helper

`dismissSystemDialog(SystemDialog.NotificationPermissionDeny)` in `core/integration/ui/.../SystemDialogUtil.kt` dismisses the system `POST_NOTIFICATIONS` permission dialog through UiAutomator. It is a top-level function with no receiver. Call it bare inside `runAppFlowTest { ... }`. Behaviour by runner:

- Robolectric (`Build.FINGERPRINT` starts with `"robolectric"`): no system dialog appears. No-op.
- Pre-Tiramisu (API < 33): `LaunchedEffect` routes directly to `onNotificationPermissionResult(true)`. No-op.
- Instrumentation on API 33 and above: locates the platform deny button by resource id (`com.android.permissioncontroller:id/permission_deny_button`) with text fallbacks (`"Don't allow"`, `"Deny"`), clicks it, then waits for the window to disappear.

Call `dismissSystemDialog` immediately after `rootRobot.acceptNotificationRationale()` whenever a test exercises the rationale Enable path. Requires `androidx.test.uiautomator:uiautomator` declared as `api(libs.androidx.uiautomator)` in `core/integration/ui` `androidMain`.

## pressBack Behaviour

`pressBack()` is a method on `BaseRobot` (`core/integration/ui/.../BaseRobot.kt`). It dispatches through `activity.onBackPressedDispatcher.onBackPressed()`. Material 3 `Dialog` and `ModalBottomSheet` windows are not registered with the activity dispatcher, so a back press bypasses the dialog and pops the underlying navigation stack. To dismiss a dialog or modal sheet, click its confirm or dismiss button, or trigger an action that closes it (for example `episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)` for the episode sheet).

The helper falls back from `Stage.RESUMED` to `Stage.PAUSED` because real Android briefly transitions through `PAUSED` while a modal is dismissing. Robolectric stays at `RESUMED`, so the fallback is a no-op there.

## Writing a Flow Test

1. Add a flow test class to `compose/flows/` that extends `BaseAppFlowTest`.
2. Each `@Test` method is one expression that wraps its body in `runAppFlowTest { ... }`.
3. At the top of the lambda, call `scenarios.X()` to register stubs before driving any robot.
4. Use robots to drive and assert UI. One robot for each screen.
5. Assert on UI state only. Do not call repositories or Data Access Objects from the test body. Do not add `@Before`.

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

For lifecycle scenarios that cross multiple surfaces, write a [journey test](journey-tests.md) instead.
