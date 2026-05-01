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
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
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
import androidx.compose.ui.test.waitUntilDoesNotExist
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.compose.ui.test.hasContentDescription as composeHasContentDescription

public const val TIMEOUT_MILLIS: Long = 5_000

/**
 * Base class for screen robots in integration tests.
 *
 * Wraps [ComposeUiTest] from `runAndroidComposeUiTest { ... }` and exposes screen-agnostic
 * primitives: actions (`click*`, `scroll*`, `swipe*`), state assertions (`assert*`), and
 * attribute checks. Subclasses add screen-specific helpers on top.
 *
 * Naming standards:
 * - Actions: `click*`, `press*`, `scroll*`, `swipe*`, `replaceText`, `inputText`.
 * - State assertions: `assert*` (e.g., `assertDisplayed`, `assertSelected`).
 * - Polling helpers: `awaitTag` (non-throwing), `assertExists` (throwing).
 *
 * Tag-based primitives all wait via [ComposeUiTest.waitUntil] before acting, so callers do not
 * need to insert their own polling loops. Defaults: `useUnmergedTree = true`,
 * `timeoutMillis = ` [TIMEOUT_MILLIS].
 *
 * @property composeUi Compose test driver under [androidx.compose.ui.test.runAndroidComposeUiTest].
 */
@OptIn(ExperimentalTestApi::class)
public abstract class BaseRobot(protected val composeUi: ComposeUiTest) {

    private fun awaitNodeWithTag(
        tag: String,
        useUnmergedTree: Boolean = true,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        composeUi.waitUntil(
            conditionDescription = "node with tag '$tag' to appear",
            timeoutMillis = timeoutMillis,
        ) {
            composeUi.onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    /**
     * Waits for the Compose test driver to drain pending recompositions and dispatched coroutines.
     */
    public fun waitForIdle() {
        composeUi.waitForIdle()
    }

    /**
     * Advances the Compose main test clock by [millis] and waits for idleness.
     */
    public fun advanceTime(millis: Long) {
        composeUi.mainClock.advanceTimeBy(millis)
        composeUi.waitForIdle()
    }

    // region Actions

    /**
     * Waits for [tag] and clicks it.
     *
     * @param useSemanticsAction If true, dispatch [SemanticsActions.OnClick] directly instead of
     *   simulating a touch event.
     */
    public fun click(tag: String, useSemanticsAction: Boolean = false) {
        awaitNodeWithTag(tag)
        val node = composeUi.onAllNodesWithTag(tag, useUnmergedTree = true).onFirst()
        if (useSemanticsAction) {
            node.performSemanticsAction(SemanticsActions.OnClick)
        } else {
            node.performClick()
        }
    }

    /**
     * Clicks the first node with [text].
     */
    public fun clickText(text: String) {
        composeUi.onNode(hasText(text)).performClick()
    }

    /**
     * Waits for [tag] and replaces the text field's contents with [text].
     */
    public fun replaceText(tag: String, text: String) {
        awaitNodeWithTag(tag)
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = true)
            .onFirst()
            .performTextReplacement(text)
    }

    /**
     * Waits for [tag] and appends [text] to the text field.
     */
    public fun inputText(tag: String, text: String) {
        awaitNodeWithTag(tag)
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = true)
            .onFirst()
            .performTextInput(text)
    }

    /**
     * Waits for [tag] and scrolls it into view.
     */
    public fun scrollTo(tag: String) {
        awaitNodeWithTag(tag)
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = true)
            .onFirst()
            .performScrollTo()
    }

    /**
     * Scrolls inside lazy list [listTag] until child [itemTag] is composed.
     */
    public fun scrollToListTag(listTag: String, itemTag: String) {
        awaitNodeWithTag(listTag)
        composeUi.onAllNodesWithTag(listTag, useUnmergedTree = true)
            .onFirst()
            .performScrollToNode(hasTestTag(itemTag))
    }

    /**
     * Performs swipe right on node with [tag].
     */
    public fun swipeRight(tag: String) {
        awaitNodeWithTag(tag)
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = true)
            .onFirst()
            .performTouchInput { swipeRight() }
    }

    /**
     * Performs swipe left on node with [tag].
     */
    public fun swipeLeft(tag: String) {
        awaitNodeWithTag(tag)
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = true)
            .onFirst()
            .performTouchInput { swipeLeft() }
    }

    /**
     * Performs swipe up on node with [tag].
     */
    public fun swipeUp(tag: String) {
        awaitNodeWithTag(tag)
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = true)
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

    /**
     * Waits for [tag] and asserts it is displayed.
     */
    public fun assertDisplayed(
        tag: String,
        timeoutMillis: Long = TIMEOUT_MILLIS,
        useUnmergedTree: Boolean = true,
    ) {
        awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
            .onFirst()
            .assertIsDisplayed()
    }

    /**
     * Asserts that node with [tag] exists but is not currently displayed (e.g., hidden by a parent).
     *
     * Differs from [assertDoesNotExist] which polls until the node is removed from the tree.
     */
    public fun assertNotDisplayed(tag: String, useUnmergedTree: Boolean = true) {
        composeUi.onNodeWithTag(tag, useUnmergedTree = useUnmergedTree).assertIsNotDisplayed()
    }

    /**
     * Waits until no node with [tag] exists.
     */
    public fun assertDoesNotExist(tag: String, timeoutMillis: Long = TIMEOUT_MILLIS) {
        composeUi.waitUntilDoesNotExist(hasTestTag(tag), timeoutMillis)
    }

    /**
     * Waits for node with [tag] to exist.
     */
    public fun assertExists(
        tag: String,
        timeoutMillis: Long = TIMEOUT_MILLIS,
        useUnmergedTree: Boolean = true,
    ) {
        awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
    }

    /**
     * Polls up to [timeoutMillis] for [tag] and returns whether it appeared. Non-throwing
     * counterpart to [assertExists]; use when the caller branches on presence.
     */
    public fun awaitTag(
        tag: String,
        timeoutMillis: Long = TIMEOUT_MILLIS,
        useUnmergedTree: Boolean = true,
    ): Boolean = runCatching { awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis) }.isSuccess

    /**
     * Waits for [tag] and asserts it is selected.
     */
    public fun assertSelected(tag: String, timeoutMillis: Long = TIMEOUT_MILLIS) {
        awaitNodeWithTag(tag, timeoutMillis = timeoutMillis)
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = true)
            .onFirst()
            .assertIsSelected()
    }

    /**
     * Waits for [tag] and asserts it is not selected.
     */
    public fun assertNotSelected(tag: String, timeoutMillis: Long = TIMEOUT_MILLIS) {
        awaitNodeWithTag(tag, timeoutMillis = timeoutMillis)
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = true)
            .onFirst()
            .assertIsNotSelected()
    }

    /**
     * Waits for [tag] and asserts it is checked / on.
     */
    public fun assertChecked(tag: String, timeoutMillis: Long = TIMEOUT_MILLIS) {
        awaitNodeWithTag(tag, timeoutMillis = timeoutMillis)
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = true)
            .onFirst()
            .assertIsOn()
    }

    /**
     * Waits for [tag] and asserts it is unchecked / off.
     */
    public fun assertUnchecked(tag: String, timeoutMillis: Long = TIMEOUT_MILLIS) {
        awaitNodeWithTag(tag, timeoutMillis = timeoutMillis)
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = true)
            .onFirst()
            .assertIsOff()
    }

    /**
     * Waits for at least one node with [text] to be displayed.
     */
    public fun assertTextDisplayed(
        text: String,
        substring: Boolean = true,
        ignoreCase: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        composeUi.waitUntil(
            conditionDescription = "node with text '$text' to appear",
            timeoutMillis = timeoutMillis,
        ) {
            composeUi.onAllNodes(hasText(text, substring = substring, ignoreCase = ignoreCase))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    /**
     * Waits for at least one node with content [description] to be displayed.
     */
    public fun assertContentDescriptionDisplayed(
        description: String,
        substring: Boolean = true,
        ignoreCase: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        composeUi.waitUntil(
            conditionDescription = "node with description '$description' to appear",
            timeoutMillis = timeoutMillis,
        ) {
            composeUi.onAllNodes(
                composeHasContentDescription(description, substring = substring, ignoreCase = ignoreCase),
            ).fetchSemanticsNodes().isNotEmpty()
        }
    }

    // endregion

    // region Attribute assertions

    /**
     * Asserts that node with [tag] has exact text equal to [text].
     */
    public fun assertTextEquals(tag: String, text: String, useUnmergedTree: Boolean = true) {
        composeUi.onNodeWithTag(tag, useUnmergedTree = useUnmergedTree).assertTextEquals(text)
    }

    /**
     * Asserts that node with [tag] has text containing [text].
     */
    public fun assertTextContains(tag: String, text: String, useUnmergedTree: Boolean = true) {
        composeUi.onNodeWithTag(tag, useUnmergedTree = useUnmergedTree).assertTextContains(text)
    }

    /**
     * Asserts that node with [tag] has any text matching [text].
     */
    public fun assertNodeHasText(tag: String, text: String, useUnmergedTree: Boolean = true) {
        composeUi.onNodeWithTag(tag, useUnmergedTree = useUnmergedTree).assert(hasText(text))
    }

    /**
     * Waits for [tag] and asserts expected content [description].
     */
    public fun assertContentDescription(
        tag: String,
        description: String,
        substring: Boolean = true,
        ignoreCase: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
        useUnmergedTree: Boolean = true,
    ) {
        awaitNodeWithTag(tag, useUnmergedTree, timeoutMillis)
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
            .onFirst()
            .assert(composeHasContentDescription(description, substring = substring, ignoreCase = ignoreCase))
    }

    /**
     * Asserts that node tagged [tag] has exactly [count] children whose tags start with [childTag].
     */
    public fun assertCountEquals(
        tag: String,
        childTag: String,
        count: Int,
        useUnmergedTree: Boolean = true,
    ) {
        composeUi.onAllNodesWithTag(tag, useUnmergedTree = useUnmergedTree)
            .onFirst()
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
