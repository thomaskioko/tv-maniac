package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.genreshows.GenreShowsTestTags

@OptIn(ExperimentalTestApi::class)
internal class GenreShowsRobot(composeUi: ComposeUiTest) : BaseRobot<GenreShowsRobot>(composeUi) {

    private val showCardPrefix = "${GenreShowsTestTags.SHOW_CARD_TEST_TAG}_"

    private fun showCardPrefixMatcher() = SemanticsMatcher("testTag starts with $showCardPrefix") { node ->
        node.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(showCardPrefix) == true
    }

    fun assertGenreShowsScreenDisplayed() = apply {
        assertDisplayed(GenreShowsTestTags.SCREEN_TEST_TAG)
    }

    fun assertTitleDisplayed(title: String) = apply {
        assertTextDisplayed(title, substring = false)
    }

    fun assertAnyShowCardDisplayed() = apply {
        awaitMatcherAtLeastOne(matcher = showCardPrefixMatcher())
    }

    fun clickFirstShowCard(): ShowDetailsRobot {
        awaitMatcherAtLeastOne(matcher = showCardPrefixMatcher())
        val tag = checkNotNull(
            composeUi.onAllNodes(matcher = showCardPrefixMatcher())
                .fetchSemanticsNodes()
                .first()
                .config
                .getOrNull(SemanticsProperties.TestTag),
        )
        click(tag)
        return ShowDetailsRobot(composeUi)
    }
}
