package com.thomaskioko.tvmaniac.testing.integration.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.swipeUp
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.compose.ui.test.hasContentDescription as composeHasContentDescription

public const val TIMEOUT_MILLIS: Long = 5_000

/**
 * Base robot for integration tests.
 *
 * Wraps [ComposeUiTest] and exposes screen-agnostic primitives: actions (`click*`, `scroll*`,
 * `swipe*`), state assertions (`assert*`), and attribute checks. Subclasses add screen-specific
 * helpers.
 *
 * Defaults follow official Compose testing guidance:
 * - Tag-based lookups read merged semantics tree. Tags should sit on merged root (Card, Button,
 *   ListItem, IconButton, anything with `Modifier.semantics(mergeDescendants = true)`) so single
 *   tag identifies single node.
 * - Tag lookups wait via [androidx.compose.ui.test.waitUntil] rather than positional `onFirst()` pick.
 *   Tag resolving to multiple nodes fails fast.
 * - Click dispatches real touch input by default ([performClick]). Use [click] only when target
 *   cannot accept touch (covered by viewport, no positioning, hidden).
 *
 * Naming standards:
 * - Actions: `click*`, `press*`, `scroll*`, `swipe*`, `replaceText`, `inputText`.
 * - State assertions: `assert*` (e.g., `assertDisplayed`, `assertSelected`).
 * - Polling helpers: `awaitTag` (non-throwing), `assertExists` (throwing).
 *
 * @property composeUi Compose test driver driving [androidx.compose.ui.test.runAndroidComposeUiTest].
 */
@OptIn(ExperimentalTestApi::class)
public abstract class BaseRobot(protected val composeUi: ComposeUiTest) {

    private fun awaitTagOnce(
        tag: String,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        composeUi.waitUntil(timeoutMillis = timeoutMillis) {
            composeUi.onAllNodes(matcher = hasTestTag(tag), useUnmergedTree = useUnmergedTree)
                .fetchSemanticsNodes().size == 1
        }
    }

    private fun awaitMatcherAtLeastOne(
        matcher: SemanticsMatcher,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        composeUi.waitUntil(timeoutMillis = timeoutMillis) {
            composeUi.onAllNodes(matcher = matcher, useUnmergedTree = useUnmergedTree)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Drains pending recompositions and dispatched coroutines. */
    public fun waitForIdle() {
        composeUi.waitForIdle()
    }

    /** Advances main test clock by [millis] and waits for idleness. */
    public fun advanceTime(millis: Long) {
        composeUi.mainClock.advanceTimeBy(millis)
        composeUi.waitForIdle()
    }

    // region Actions

    /**
     * Waits for [tag] to resolve to exactly one node, then dispatches [SemanticsActions.OnClick].
     *
     * Semantics action keeps dispatch deterministic under Robolectric where synthetic touch input
     * on overlapping modifiers does not always reach right handler. Use [clickWithTouch] for
     * real touch input.
     */
    public fun click(tag: String, useUnmergedTree: Boolean = false) {
        awaitTagOnce(tag, useUnmergedTree)
        composeUi.onNode(matcher = hasTestTag(tag), useUnmergedTree = useUnmergedTree)
            .performSemanticsAction(SemanticsActions.OnClick)
    }

    /**
     * Waits for [tag] and dispatches synthetic touch click via [performClick].
     *
     * Use for real touch input, overlapping pointer handlers, or verifying ripple positioning.
     */
    public fun clickWithTouch(tag: String, useUnmergedTree: Boolean = false) {
        awaitTagOnce(tag, useUnmergedTree)
        composeUi.onNode(matcher = hasTestTag(tag), useUnmergedTree = useUnmergedTree).performClick()
    }

    /** Clicks first node whose text matches [text]. */
    public fun clickText(text: String, useUnmergedTree: Boolean = false) {
        composeUi.onNode(hasText(text), useUnmergedTree = useUnmergedTree).performClick()
    }

    /** Waits for [tag] and replaces text field contents with [text]. */
    public fun replaceText(tag: String, text: String, useUnmergedTree: Boolean = false) {
        awaitTagOnce(tag, useUnmergedTree)
        composeUi.onNode(hasTestTag(tag), useUnmergedTree = useUnmergedTree).performTextReplacement(text)
    }

    /** Waits for [tag] and appends [text] to text field. */
    public fun inputText(tag: String, text: String, useUnmergedTree: Boolean = false) {
        awaitTagOnce(tag, useUnmergedTree)
        composeUi.onNode(hasTestTag(tag), useUnmergedTree = useUnmergedTree).performTextInput(text)
    }

    /** Waits for [tag] and scrolls it into view. */
    public fun scrollTo(tag: String, useUnmergedTree: Boolean = false) {
        awaitTagOnce(tag, useUnmergedTree)
        composeUi.onNode(matcher = hasTestTag(tag), useUnmergedTree = useUnmergedTree).performScrollTo()
    }

    /** Scrolls inside lazy list [listTag] until child [itemTag] is composed. */
    public fun scrollToListTag(listTag: String, itemTag: String, useUnmergedTree: Boolean = false) {
        awaitTagOnce(listTag, useUnmergedTree)
        composeUi.onNode(matcher = hasTestTag(listTag), useUnmergedTree = useUnmergedTree)
            .performScrollToNode(matcher = hasTestTag(itemTag))
    }

    /** Performs right swipe gesture on node with [tag]. */
    public fun swipeRight(tag: String, useUnmergedTree: Boolean = false) {
        awaitTagOnce(tag, useUnmergedTree)
        composeUi.onNode(matcher = hasTestTag(tag), useUnmergedTree = useUnmergedTree).performTouchInput { swipeRight() }
    }

    /** Performs left swipe gesture on node with [tag]. */
    public fun swipeLeft(tag: String, useUnmergedTree: Boolean = false) {
        awaitTagOnce(tag, useUnmergedTree)
        composeUi.onNode(matcher = hasTestTag(tag), useUnmergedTree = useUnmergedTree).performTouchInput { swipeLeft() }
    }

    /** Performs upward swipe gesture on node with [tag]. */
    public fun swipeUp(tag: String, useUnmergedTree: Boolean = false) {
        awaitTagOnce(tag, useUnmergedTree)
        composeUi.onNode(matcher = hasTestTag(tag), useUnmergedTree = useUnmergedTree).performTouchInput { swipeUp() }
    }

    /**
     * Dispatches back press on foreground [ComponentActivity].
     *
     * Falls back from `RESUMED` to `PAUSED` for transitions like bottom sheets or dialogs.
     */
    public fun pressBack() {
        composeUi.waitForIdle()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val monitor = ActivityLifecycleMonitorRegistry.getInstance()
            val activity = (
                monitor.getActivitiesInStage(Stage.RESUMED).firstOrNull()
                    ?: monitor.getActivitiesInStage(Stage.PAUSED).firstOrNull()
                ) as? ComponentActivity
                ?: error("pressBack: no resumed or paused ComponentActivity found")
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeUi.waitForIdle()
    }

    // endregion

    // region State assertions

    /** Waits for [tag] and asserts resolved node is displayed. */
    public fun assertDisplayed(
        tag: String,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        awaitTagOnce(tag, useUnmergedTree, timeoutMillis)
        composeUi.onNode(hasTestTag(tag), useUnmergedTree = useUnmergedTree).assertIsDisplayed()
    }

    /**
     * Asserts node with [tag] exists but is not currently displayed.
     *
     * Differs from [assertDoesNotExist], which polls until node is removed.
     */
    public fun assertNotDisplayed(tag: String, useUnmergedTree: Boolean = false) {
        composeUi.onNode(matcher = hasTestTag(tag), useUnmergedTree = useUnmergedTree).assertIsNotDisplayed()
    }

    /** Waits until no node with [tag] exists. */
    public fun assertDoesNotExist(
        tag: String,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        composeUi.waitUntil(timeoutMillis = timeoutMillis) {
            composeUi.onAllNodes(matcher = hasTestTag(tag), useUnmergedTree = useUnmergedTree)
                .fetchSemanticsNodes().isEmpty()
        }
    }

    /** Waits for node with [tag] to exist. */
    public fun assertExists(
        tag: String,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        awaitTagOnce(tag, useUnmergedTree, timeoutMillis)
    }

    /**
     * Polls for [tag] and returns whether it appeared.
     *
     * Non-throwing counterpart to [assertExists].
     */
    public fun awaitTag(
        tag: String,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ): Boolean =
        runCatching { awaitTagOnce(tag, useUnmergedTree, timeoutMillis) }.isSuccess

    /** Waits for [tag] and asserts it is selected. */
    public fun assertSelected(
        tag: String,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        awaitTagOnce(tag, useUnmergedTree, timeoutMillis)
        composeUi.onNode(hasTestTag(tag), useUnmergedTree = useUnmergedTree).assertIsSelected()
    }

    /** Waits for [tag] and asserts it is not selected. */
    public fun assertNotSelected(
        tag: String,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        awaitTagOnce(tag, useUnmergedTree, timeoutMillis)
        composeUi.onNode(hasTestTag(tag), useUnmergedTree = useUnmergedTree).assertIsNotSelected()
    }

    /** Waits for [tag] and asserts it is checked. */
    public fun assertChecked(
        tag: String,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        awaitTagOnce(tag, useUnmergedTree, timeoutMillis)
        composeUi.onNode(hasTestTag(tag), useUnmergedTree = useUnmergedTree).assertIsOn()
    }

    /** Waits for [tag] and asserts it is unchecked. */
    public fun assertUnchecked(
        tag: String,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        awaitTagOnce(tag, useUnmergedTree, timeoutMillis)
        composeUi.onNode(hasTestTag(tag), useUnmergedTree = useUnmergedTree).assertIsOff()
    }

    /** Waits for at least one node with [text] to be displayed. */
    public fun assertTextDisplayed(
        text: String,
        substring: Boolean = true,
        ignoreCase: Boolean = false,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        awaitMatcherAtLeastOne(
            matcher = hasText(text, substring = substring, ignoreCase = ignoreCase),
            useUnmergedTree = useUnmergedTree,
            timeoutMillis = timeoutMillis,
        )
    }

    /**
     * Waits for at least one node with content [description] to be displayed.
     *
     * Unused at Phase 1; kept for Phase 4 snackbar wiring.
     */
    public fun assertContentDescriptionDisplayed(
        description: String,
        substring: Boolean = true,
        ignoreCase: Boolean = false,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        awaitMatcherAtLeastOne(
            matcher = composeHasContentDescription(description, substring = substring, ignoreCase = ignoreCase),
            useUnmergedTree = useUnmergedTree,
            timeoutMillis = timeoutMillis,
        )
    }

    // endregion

    // region Attribute assertions

    /** Asserts node with [tag] has exact text matching [text]. */
    public fun assertTextEquals(tag: String, text: String, useUnmergedTree: Boolean = false) {
        composeUi.onNode(hasTestTag(tag), useUnmergedTree = useUnmergedTree).assertTextEquals(text)
    }

    /** Asserts node with [tag] has text containing [text]. */
    public fun assertTextContains(tag: String, text: String, useUnmergedTree: Boolean = false) {
        composeUi.onNode(hasTestTag(tag), useUnmergedTree = useUnmergedTree).assertTextContains(text)
    }

    /**
     * Waits for [tag] and asserts resolved node has text matching [text].
     *
     * Defaults to substring matching. safe to call after actions triggering async recomposition.
     */
    public fun assertNodeHasText(
        tag: String,
        text: String,
        substring: Boolean = true,
        ignoreCase: Boolean = false,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        val matcher = hasTestTag(tag) and hasText(text, substring = substring, ignoreCase = ignoreCase)
        composeUi.waitUntil(timeoutMillis = timeoutMillis) {
            composeUi.onAllNodes(matcher, useUnmergedTree = useUnmergedTree)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * Waits for [tag] and asserts expected content [description].
     *
     * Slated for removal after Phase 5 if unused.
     */
    @Deprecated("Unused; remove after Phase 5 if no consumer appears.")
    public fun assertContentDescription(
        tag: String,
        description: String,
        substring: Boolean = true,
        ignoreCase: Boolean = false,
        useUnmergedTree: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        awaitTagOnce(tag, useUnmergedTree, timeoutMillis)
        composeUi.onNode(hasTestTag(tag), useUnmergedTree = useUnmergedTree)
            .assert(composeHasContentDescription(description, substring = substring, ignoreCase = ignoreCase))
    }

    /**
     * Asserts node tagged [tag] has exactly [count] children whose tags start with [childTag].
     *
     * Walks merged-tree children via [onChildren]. visual layout order.
     */
    public fun assertCountEquals(
        tag: String,
        childTag: String,
        count: Int,
        useUnmergedTree: Boolean = false,
    ) {
        composeUi.onNode(hasTestTag(tag), useUnmergedTree = useUnmergedTree)
            .onChildren()
            .filter(
                SemanticsMatcher("testTag starts with $childTag") { node: SemanticsNode ->
                    node.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(childTag) == true
                },
            )
            .assertCountEquals(count)
    }

    // endregion
}
