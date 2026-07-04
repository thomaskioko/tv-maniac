package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.ratingsheet.RatingSheetTestTags

@OptIn(ExperimentalTestApi::class)
internal class RatingSheetRobot(composeUi: ComposeUiTest) : BaseRobot<RatingSheetRobot>(composeUi) {

    fun assertSheetDisplayed() = apply {
        awaitTagOnce(RatingSheetTestTags.SHEET_TEST_TAG)
        assertDisplayed(RatingSheetTestTags.SHEET_TEST_TAG)
        waitForIdle()
    }

    fun assertSheetDoesNotExist() = apply {
        assertDoesNotExist(RatingSheetTestTags.SHEET_TEST_TAG)
    }

    fun assertClearRatingButtonDisplayed() = apply {
        assertDisplayed(RatingSheetTestTags.CLEAR_RATING_BUTTON)
    }

    fun assertClearRatingButtonDoesNotExist() = apply {
        assertDoesNotExist(RatingSheetTestTags.CLEAR_RATING_BUTTON)
    }

    fun clickStar(rating: Int) = apply {
        click(RatingSheetTestTags.starRating(rating))
    }

    fun clickClearRatingButton() = apply {
        click(RatingSheetTestTags.CLEAR_RATING_BUTTON)
    }
}
