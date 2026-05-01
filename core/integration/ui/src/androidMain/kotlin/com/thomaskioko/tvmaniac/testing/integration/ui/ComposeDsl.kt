@file:OptIn(ExperimentalTestApi::class)

package com.thomaskioko.tvmaniac.testing.integration.ui

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.swipeUp
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import androidx.compose.ui.test.hasContentDescription as composeHasContentDescription

/**
 * Integration test primitives for Compose.
 *
 * Standards:
 * - Actions: `on*`, `perform*`, `click*`, `press*`, `scroll*`, `input*`, `replace*`, `submit*`.
 * - State assertions: `is*` (e.g., `isShown`, `isEnabled`).
 * - Attribute assertions: `has*` (e.g., `hasText`, `hasCount`).
 * - `exists(tag)`: presence-only check.
 *
 * Implementation:
 * - Extensions on [ComposeContentTestRule] returning `this` for chaining.
 * - All tag-based primitives call [awaitNodeWithTag] for race-safety.
 * - Defaults: `useUnmergedTree = true`, `timeoutMillis = 5_000`.
 */

private const val DEFAULT_TIMEOUT_MS: Long = 10_000

/**
 * Polls until at least one node with [tag] exists.
 *
 * @param tag Test tag to wait for.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 * @param timeoutMillis Maximum time to wait for node to appear.
 */
private fun ComposeContentTestRule.awaitNodeWithTag(
    tag: String,
    useUnmergedTree: Boolean,
    timeoutMillis: Long,
) {
    waitUntil(timeoutMillis) {
        onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
            .fetchSemanticsNodes()
            .isNotEmpty()
    }
}

// region Actions

/**
 * Waits for [tag] and performs click.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 * @param useSemanticsAction If true, dispatches [SemanticsActions.OnClick] directly.
 */
public fun ComposeContentTestRule.onClick(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
    useSemanticsAction: Boolean = false,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    val node = onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree).onFirst()
    if (useSemanticsAction) {
        node.performSemanticsAction(SemanticsActions.OnClick)
    } else {
        node.performClick()
    }
}

/**
 * Waits for [tag] and performs long-press.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.performLongClick(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .performTouchInput { longClick() }
}

/**
 * Waits for [tag] and appends [text].
 *
 * @param tag Test tag of text field.
 * @param text Text to append.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.inputText(
    tag: String,
    text: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .performTextInput(text)
}

/**
 * Waits for [tag] and replaces text with [text].
 *
 * @param tag Test tag of text field.
 * @param text New text content.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.replaceText(
    tag: String,
    text: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .performTextReplacement(text)
}

/**
 * Waits for [tag] and triggers IME action.
 *
 * @param tag Test tag of text field.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.submitText(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .performImeAction()
}

/**
 * Waits for [tag] and scrolls it into view.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.scrollTo(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .performScrollTo()
}

/**
 * Scrolls lazy list [listTag] until child [itemTag] is composed.
 *
 * @param listTag Test tag of scrollable container.
 * @param itemTag Test tag of child node.
 */
public fun ComposeContentTestRule.scrollTo(
    listTag: String,
    itemTag: String,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(listTag, useUnmergedTree = true, timeoutMillis = DEFAULT_TIMEOUT_MS)
    onAllNodesWithTag(listTag, useUnmergedTree = true)
        .onFirst()
        .performScrollToNode(hasTestTag(itemTag))
}

/**
 * Waits for [tag] and performs swipe right.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.swipeRight(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .performTouchInput { swipeRight() }
}

/**
 * Waits for [tag] and performs swipe up.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.swipeUp(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .performTouchInput { swipeUp() }
}

/**
 * Dispatches back press on the foreground [ComponentActivity].
 *
 * Falls back from `RESUMED` to `PAUSED` because on real Android the activity briefly transitions
 * to `PAUSED` while a `ModalBottomSheet` or platform `Dialog` is mid-dismiss; under Robolectric
 * the transition is synchronous and the activity stays `RESUMED`. Without the fallback the helper
 * races on instrumentation and throws "no resumed ComponentActivity found" right after the modal
 * dismisses but before the activity returns to `RESUMED`.
 */
public fun ComposeContentTestRule.pressBack(): ComposeContentTestRule = apply {
    waitForIdle()
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        val monitor = ActivityLifecycleMonitorRegistry.getInstance()
        val activity = (
            monitor.getActivitiesInStage(Stage.RESUMED).firstOrNull()
                ?: monitor.getActivitiesInStage(Stage.PAUSED).firstOrNull()
            ) as? ComponentActivity
            ?: error("pressBack: no resumed or paused ComponentActivity found")
        activity.onBackPressedDispatcher.onBackPressed()
    }
    waitForIdle()
}

// endregion

// region System dialogs

/**
 * Catalogue of platform dialogs that integration tests may need to dismiss.
 *
 * Each entry carries the UiAutomator selectors needed to locate its action button. Callers stay
 * declarative ("dismiss the notification permission prompt") instead of dealing with resource ids
 * or locale-specific button text. Add new entries here as new dialogs surface in flow tests.
 */
public enum class SystemDialog(internal val selectors: List<BySelector>) {
    /** Android 13+ POST_NOTIFICATIONS prompt. Locates the platform deny button. */
    NotificationPermissionDeny(
        selectors = listOf(
            By.res("com.android.permissioncontroller", "permission_deny_button"),
            By.textContains("Don't allow"),
            By.textContains("Deny"),
        ),
    ),
}

/**
 * Clicks the action button for [dialog] via UiAutomator and waits for it to disappear.
 *
 * Use to dismiss platform dialogs that detach the Compose owner. The Compose test rule cannot
 * drive these because they live outside the Compose hierarchy. The dialog catalogue ([SystemDialog])
 * encodes the selectors so call sites do not import UiAutomator.
 *
 * No-op on Robolectric and pre-Tiramisu where no real system dialog appears. If no selector
 * resolves within [appearTimeoutMillis], returns silently. The next caller assertion will surface
 * the real symptom if the dialog is still up.
 *
 * @param dialog Catalogue entry whose action button to click.
 * @param appearTimeoutMillis Maximum wait per selector for a match before giving up.
 * @param dismissTimeoutMillis Maximum wait for the matched selector to disappear after the click.
 */
public fun ComposeContentTestRule.dismissSystemDialog(
    dialog: SystemDialog,
    appearTimeoutMillis: Long = 1_000,
    dismissTimeoutMillis: Long = 1_000,
): ComposeContentTestRule = apply {
    if (Build.FINGERPRINT.startsWith("robolectric", ignoreCase = true)) return@apply
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@apply

    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    var matched: BySelector? = null
    val target = dialog.selectors.firstNotNullOfOrNull { selector ->
        device.wait(Until.findObject(selector), appearTimeoutMillis)?.also { matched = selector }
    } ?: return@apply

    target.click()
    matched?.let { device.wait(Until.gone(it), dismissTimeoutMillis) }
}

// endregion

// region Time advancement

/**
 * Advances clock by [millis] and waits for idleness.
 */
public fun ComposeContentTestRule.advanceTimeBy(millis: Long): ComposeContentTestRule = apply {
    mainClock.advanceTimeBy(millis)
    waitForIdle()
}

// endregion

// region State assertions (is*)

/**
 * Waits for [tag] and asserts it is displayed.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.isShown(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .assertIsDisplayed()
}

/**
 * Waits until no node with [tag] exists.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 */
public fun ComposeContentTestRule.isHidden(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
): ComposeContentTestRule = apply {
    waitUntilDoesNotExist(hasTestTag(tag), timeoutMillis)
}

/**
 * Waits for [tag] to exist.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.exists(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
}

/**
 * Polls up to [timeoutMillis] for [tag] and returns whether it appeared.
 *
 * Non-throwing counterpart to [exists]. Use when the caller branches on presence rather than
 * fails the test (e.g., overlays that surface only on some runners or environments).
 *
 * @param tag Test tag to wait for.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.awaitTag(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): Boolean = runCatching {
    waitUntil(timeoutMillis) {
        onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
            .fetchSemanticsNodes()
            .isNotEmpty()
    }
}.isSuccess

/**
 * Waits for [tag] and asserts it is enabled.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.isEnabled(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .assertIsEnabled()
}

/**
 * Waits for [tag] and asserts it is disabled.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.isDisabled(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .assertIsNotEnabled()
}

/**
 * Waits for [tag] and asserts it is selected.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.isSelected(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .assertIsSelected()
}

/**
 * Waits for [tag] and asserts it is not selected.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.isNotSelected(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .assertIsNotSelected()
}

/**
 * Waits for [tag] and asserts it is checked.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.isChecked(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .assertIsOn()
}

/**
 * Waits for [tag] and asserts it is unchecked.
 *
 * @param tag Test tag of node.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.isUnchecked(
    tag: String,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .assertIsOff()
}

/**
 * Waits for at least one node with [text] to be displayed.
 *
 * @param text Text to search for.
 * @param substring Whether to match [text] as substring.
 * @param ignoreCase Whether to ignore case.
 * @param timeoutMillis Maximum wait time.
 */
public fun ComposeContentTestRule.isTextShown(
    text: String,
    substring: Boolean = true,
    ignoreCase: Boolean = false,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
): ComposeContentTestRule = apply {
    waitUntil(timeoutMillis) {
        onAllNodes(hasText(text, substring = substring, ignoreCase = ignoreCase))
            .fetchSemanticsNodes().isNotEmpty()
    }
}

/**
 * Waits for at least one node with [description] to be displayed.
 *
 * @param description Content description to search for.
 * @param substring Whether to match [description] as substring.
 * @param ignoreCase Whether to ignore case.
 * @param timeoutMillis Maximum wait time.
 */
public fun ComposeContentTestRule.isContentDescriptionShown(
    description: String,
    substring: Boolean = true,
    ignoreCase: Boolean = false,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
): ComposeContentTestRule = apply {
    waitUntil(timeoutMillis) {
        onAllNodes(composeHasContentDescription(description, substring = substring, ignoreCase = ignoreCase))
            .fetchSemanticsNodes().isNotEmpty()
    }
}

// endregion

// region Attribute assertions (has*)

/**
 * Asserts that [tag] has exactly [count] children whose tag starts with [prefix].
 *
 * @param tag Test tag of parent node.
 * @param prefix Prefix for children's test tags.
 * @param count Expected number of children.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.hasCount(
    tag: String,
    prefix: String,
    count: Int,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .onChildren()
        .filter(
            SemanticsMatcher("testTag starts with $prefix") { node: SemanticsNode ->
                node.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(prefix) == true
            },
        )
        .assertCountEquals(count)
}

/**
 * Waits for [tag] and asserts expected [description].
 *
 * @param tag Test tag of node.
 * @param description Expected content description.
 * @param substring Whether to match as substring.
 * @param ignoreCase Whether to ignore case.
 * @param timeoutMillis Maximum wait time.
 * @param useUnmergedTree Whether to search unmerged semantics tree.
 */
public fun ComposeContentTestRule.hasContentDescription(
    tag: String,
    description: String,
    substring: Boolean = true,
    ignoreCase: Boolean = false,
    timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
    useUnmergedTree: Boolean = true,
): ComposeContentTestRule = apply {
    awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
        .onFirst()
        .assert(composeHasContentDescription(description, substring = substring, ignoreCase = ignoreCase))
}

// endregion
