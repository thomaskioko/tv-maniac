package com.thomaskioko.tvmaniac.ratingsheet.presenter

import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.domain.ratings.ShouldPromptForRatingInteractor
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetParam
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetRoute

public suspend fun Navigator.promptForEpisodeRating(
    interactor: ShouldPromptForRatingInteractor,
    showId: Long,
    episodeId: Long,
) {
    val shouldPrompt = interactor(
        ShouldPromptForRatingInteractor.Param(showId = showId, episodeId = episodeId),
    )
    if (shouldPrompt) {
        navigateTo(RatingSheetRoute(RatingSheetParam(ratingType = RatingEntityType.EPISODE, id = episodeId)))
    }
}
