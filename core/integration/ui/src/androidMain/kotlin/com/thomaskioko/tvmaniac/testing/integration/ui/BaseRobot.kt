package com.thomaskioko.tvmaniac.testing.integration.ui

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performClick

private const val TIMEOUT_MILLIS: Long = 1_500

/**
 * Base class for screen robots in integration tests.
 *
 * Wraps [ComposeContentTestRule] and exposes [ComposeTestDsl][com.thomaskioko.tvmaniac.testing.integration.ui.util]
 * primitives. Subclasses add screen-specific actions and assertions.
 *
 * @property composeTestRule Compose rule driving the activity under test.
 */
public abstract class BaseRobot(protected val composeTestRule: ComposeContentTestRule) {

    public companion object {
        /**
         * Toggle this to true to slow down connected tests for visual inspection during development.
         */
        public var isDevelopmentMode: Boolean = false
    }

    /**
     * Pauses the test execution for a short duration if [isDevelopmentMode] is true.
     * This is useful for visually inspecting connected tests on an emulator.
     */
    protected fun devSleep(duration: Long = 1000) {
        if (isDevelopmentMode) {
            Thread.sleep(duration)
        }
    }

    /**
     * Advances Compose test clock by [millis] and waits for idleness.
     */
    public fun advanceTime(millis: Long) {
        composeTestRule.advanceTimeBy(millis)
    }

    /**
     * Asserts that at least one node with [text] is displayed.
     *
     * @param text Text to search for.
     * @param substring If true, match [text] as substring.
     * @param ignoreCase If true, ignore case while matching.
     * @param timeoutMillis Maximum time to wait for text to appear.
     */
    public fun verifyTextShown(
        text: String,
        substring: Boolean = true,
        ignoreCase: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        composeTestRule.isTextShown(
            text = text,
            substring = substring,
            ignoreCase = ignoreCase,
            timeoutMillis = timeoutMillis,
        )
        devSleep()
    }

    /**
     * Asserts that at least one node with content [description] is displayed.
     *
     * @param description Content description to search for.
     * @param substring If true, match [description] as substring.
     * @param ignoreCase If true, ignore case while matching.
     * @param timeoutMillis Maximum time to wait for node to appear.
     */
    public fun verifyContentDescriptionShown(
        description: String,
        substring: Boolean = true,
        ignoreCase: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
    ) {
        composeTestRule.isContentDescriptionShown(
            description = description,
            substring = substring,
            ignoreCase = ignoreCase,
            timeoutMillis = timeoutMillis,
        )
        devSleep()
    }

    /**
     * Dispatches system back press.
     */
    public fun pressBack() {
        devSleep()
        composeTestRule.pressBack()
    }

    public fun verifyTagShown(
        tag: String,
        timeoutMillis: Long = TIMEOUT_MILLIS,
        useUnmergedTree: Boolean = true,
    ) {
        composeTestRule.isShown(tag, timeoutMillis = timeoutMillis, useUnmergedTree = useUnmergedTree)
    }

    /**
     * Waits until no node with [tag] exists.
     *
     * @param tag Test tag of the node.
     * @param timeoutMillis Maximum time to wait for node to disappear.
     */
    public fun verifyTagHidden(tag: String, timeoutMillis: Long = TIMEOUT_MILLIS) {
        composeTestRule.isHidden(tag, timeoutMillis = timeoutMillis)
        devSleep()
    }

    /**
     * Waits for node with [tag] to exist.
     *
     * @param tag Test tag of the node.
     * @param timeoutMillis Maximum time to wait for node to appear.
     * @param useUnmergedTree Whether to search unmerged semantics tree.
     */
    public fun verifyTagExists(
        tag: String,
        timeoutMillis: Long = TIMEOUT_MILLIS,
        useUnmergedTree: Boolean = true,
    ) {
        composeTestRule.exists(tag, timeoutMillis = timeoutMillis, useUnmergedTree = useUnmergedTree)
        devSleep()
    }

    /**
     * Polls up to [timeoutMillis] for [tag] and returns whether it appeared.
     */
    public fun awaitTag(
        tag: String,
        timeoutMillis: Long = TIMEOUT_MILLIS,
        useUnmergedTree: Boolean = true,
    ): Boolean = composeTestRule.awaitTag(tag, timeoutMillis, useUnmergedTree)

    /**
     * Performs swipe right on node with [tag].
     */
    public fun swipeRight(tag: String) {
        composeTestRule.swipeRight(tag)
        devSleep()
    }

    /**
     * Performs swipe up on node with [tag].
     */
    public fun swipeUp(tag: String) {
        composeTestRule.swipeUp(tag)
        devSleep()
    }

    /**
     * Asserts that node tagged [tag] has exactly [count] children whose tags start with [childTag].
     * @param tag Test tag of the parent node.
     * @param childTag Prefix for matching children's test tags.
     * @param count Expected number of children.
     * @param useUnmergedTree Whether to search unmerged semantics tree.
     */
    public fun verifyCount(
        tag: String,
        childTag: String,
        count: Int,
        useUnmergedTree: Boolean = true,
    ) {
        composeTestRule.hasCount(tag, childTag, count, useUnmergedTree = useUnmergedTree)
        devSleep()
    }

    /**
     * Asserts that node tagged [tag] has expected content [description].
     *
     * @param tag Test tag of the node.
     * @param description Expected content description.
     * @param substring If true, match [description] as substring.
     * @param ignoreCase If true, ignore case while matching.
     * @param timeoutMillis Maximum time to wait for node to appear.
     * @param useUnmergedTree Whether to search unmerged semantics tree.
     */
    public fun verifyContentDescription(
        tag: String,
        description: String,
        substring: Boolean = true,
        ignoreCase: Boolean = false,
        timeoutMillis: Long = TIMEOUT_MILLIS,
        useUnmergedTree: Boolean = true,
    ) {
        composeTestRule.hasContentDescription(
            tag = tag,
            description = description,
            substring = substring,
            ignoreCase = ignoreCase,
            timeoutMillis = timeoutMillis,
            useUnmergedTree = useUnmergedTree,
        )
        devSleep()
    }

    /**
     * Clicks node with [tag].
     *
     * @param tag Test tag of the node.
     * @param useSemanticsAction If true, dispatch on-click semantics action directly.
     */
    public fun click(tag: String, useSemanticsAction: Boolean = false) {
        composeTestRule.onClick(tag, useSemanticsAction = useSemanticsAction)
        devSleep()
    }

    /**
     * Clicks node with [text].
     *
     * @param text Text of the node.
     */
    public fun clickText(text: String) {
        composeTestRule.onNode(hasText(text)).performClick()
        devSleep()
    }

    /**
     * Scrolls node with [tag] into view.
     */
    public fun scrollTo(tag: String) {
        composeTestRule.scrollTo(tag)
        devSleep()
    }

    /**
     * Scrolls inside lazy list [listTag] until child [itemTag] is composed.
     */
    public fun scrollToListTag(listTag: String, itemTag: String) {
        composeTestRule.scrollTo(listTag, itemTag)
        devSleep()
    }
}
