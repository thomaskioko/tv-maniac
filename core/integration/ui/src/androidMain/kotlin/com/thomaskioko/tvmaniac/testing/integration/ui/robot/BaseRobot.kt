package com.thomaskioko.tvmaniac.testing.integration.ui.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.util.advanceTimeBy
import com.thomaskioko.tvmaniac.testing.integration.ui.util.exists
import com.thomaskioko.tvmaniac.testing.integration.ui.util.hasContentDescription
import com.thomaskioko.tvmaniac.testing.integration.ui.util.hasCount
import com.thomaskioko.tvmaniac.testing.integration.ui.util.isContentDescriptionShown
import com.thomaskioko.tvmaniac.testing.integration.ui.util.isHidden
import com.thomaskioko.tvmaniac.testing.integration.ui.util.isShown
import com.thomaskioko.tvmaniac.testing.integration.ui.util.isTextShown
import com.thomaskioko.tvmaniac.testing.integration.ui.util.onClick
import com.thomaskioko.tvmaniac.testing.integration.ui.util.pressBack
import com.thomaskioko.tvmaniac.testing.integration.ui.util.scrollTo

/**
 * Base class for screen robots in integration tests.
 *
 * Wraps [ComposeContentTestRule] and exposes [ComposeTestDsl][com.thomaskioko.tvmaniac.testing.integration.ui.util]
 * primitives. Subclasses add screen-specific actions and assertions.
 *
 * @property composeTestRule Compose rule driving the activity under test.
 */
public abstract class BaseRobot(protected val composeTestRule: ComposeContentTestRule) {

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
        timeoutMillis: Long = 5_000,
    ) {
        composeTestRule.isTextShown(
            text = text,
            substring = substring,
            ignoreCase = ignoreCase,
            timeoutMillis = timeoutMillis,
        )
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
        timeoutMillis: Long = 5_000,
    ) {
        composeTestRule.isContentDescriptionShown(
            description = description,
            substring = substring,
            ignoreCase = ignoreCase,
            timeoutMillis = timeoutMillis,
        )
    }

    /**
     * Dispatches system back press.
     */
    public fun pressBack() {
        composeTestRule.pressBack()
    }

    /**
     * Asserts that node with [tag] is displayed.
     *
     * @param tag Test tag of the node.
     * @param timeoutMillis Maximum time to wait for node to appear.
     * @param useUnmergedTree Whether to search unmerged semantics tree.
     */
    public fun verifyTagShown(
        tag: String,
        timeoutMillis: Long = 5_000,
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
    public fun verifyTagHidden(tag: String, timeoutMillis: Long = 5_000) {
        composeTestRule.isHidden(tag, timeoutMillis = timeoutMillis)
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
        timeoutMillis: Long = 5_000,
        useUnmergedTree: Boolean = true,
    ) {
        composeTestRule.exists(tag, timeoutMillis = timeoutMillis, useUnmergedTree = useUnmergedTree)
    }

    /**
     * Asserts that node tagged [tag] has exactly [count] children whose tags start with [childTag].
     *
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
        timeoutMillis: Long = 5_000,
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
    }

    /**
     * Clicks node with [tag].
     *
     * @param tag Test tag of the node.
     * @param useSemanticsAction If true, dispatch on-click semantics action directly.
     */
    protected fun click(tag: String, useSemanticsAction: Boolean = false) {
        composeTestRule.onClick(tag, useSemanticsAction = useSemanticsAction)
    }

    /**
     * Scrolls node with [tag] into view.
     */
    protected fun scrollToTag(tag: String) {
        composeTestRule.scrollTo(tag)
    }

    /**
     * Scrolls inside lazy list [listTag] until child [itemTag] is composed.
     */
    protected fun scrollToListTag(listTag: String, itemTag: String) {
        composeTestRule.scrollTo(listTag, itemTag)
    }
}
