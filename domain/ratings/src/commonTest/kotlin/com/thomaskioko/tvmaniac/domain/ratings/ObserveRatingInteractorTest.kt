package com.thomaskioko.tvmaniac.domain.ratings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.data.ratings.api.EpisodeRating
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.data.ratings.api.SeasonRating
import com.thomaskioko.tvmaniac.data.ratings.api.ShowRating
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ObserveRatingInteractorTest {
    private val ratingsRepository = FakeRatingsRepository()

    private val interactor = ObserveRatingInteractor(
        ratingsRepository = ratingsRepository,
    )

    @Test
    fun `should emit user rating given show has rating`() = runTest {
        ratingsRepository.setShowRating(
            ShowRating(userRating = 8, communityRating = 7.5, communityVotes = 1200L, pendingAction = PendingAction.NOTHING),
        )

        interactor(ObserveRatingInteractor.Param(type = RatingEntityType.SHOW, id = 84958L))

        interactor.flow.test {
            awaitItem() shouldBe 8
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit user rating given season has rating`() = runTest {
        ratingsRepository.setSeasonRating(SeasonRating(userRating = 9, pendingAction = PendingAction.NOTHING))

        interactor(ObserveRatingInteractor.Param(type = RatingEntityType.SEASON, id = 1L))

        interactor.flow.test {
            awaitItem() shouldBe 9
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit user rating given episode has rating`() = runTest {
        ratingsRepository.setEpisodeRating(EpisodeRating(userRating = 7, pendingAction = PendingAction.NOTHING))

        interactor(ObserveRatingInteractor.Param(type = RatingEntityType.EPISODE, id = 1L))

        interactor.flow.test {
            awaitItem() shouldBe 7
            cancelAndConsumeRemainingEvents()
        }
    }
}
