@file:OptIn(ExperimentalTestApi::class)

package com.thomaskioko.tvmaniac.testing.integration.ui.util

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
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
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

private const val DEFAULT_TIMEOUT_MS: Long = 5_000

/**
 * Polls until exactly one node with [tag] exists.
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
    if (useUnmergedTree) {
        waitUntil(timeoutMillis) {
            onAllNodesWithTag(tag, useUnmergedTree = true).fetchSemanticsNodes().size == 1
        }
    } else {
        waitUntilExactlyOneExists(hasTestTag(tag), timeoutMillis)
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
    if (useSemanticsAction) {
        onNodeWithTag(tag, useUnmergedTree = useUnmergedTree)
            .performSemanticsAction(SemanticsActions.OnClick)
    } else {
        onNodeWithTag(tag, useUnmergedTree).performClick()
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree)
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree)
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree)
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree)
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
    onNodeWithTag(tag, useUnmergedTree).performScrollTo()
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
    onNodeWithTag(listTag, useUnmergedTree = true)
        .performScrollToNode(hasTestTag(itemTag))
}

/**
 * Dispatches back press on resumed [ComponentActivity].
 */
public fun ComposeContentTestRule.pressBack(): ComposeContentTestRule = apply {
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        val activity = ActivityLifecycleMonitorRegistry.getInstance()
            .getActivitiesInStage(Stage.RESUMED)
            .firstOrNull() as? ComponentActivity
            ?: error("pressBack: no resumed ComponentActivity found")
        activity.onBackPressedDispatcher.onBackPressed()
    }
    waitForIdle()
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree).assertIsDisplayed()
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree).assertIsEnabled()
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree).assertIsNotEnabled()
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree).assertIsSelected()
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree).assertIsNotSelected()
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree).assertIsOn()
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree).assertIsOff()
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree)
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
    onNodeWithTag(tag, useUnmergedTree = useUnmergedTree)
        .assert(composeHasContentDescription(description, substring = substring, ignoreCase = ignoreCase))
}

// endregion
