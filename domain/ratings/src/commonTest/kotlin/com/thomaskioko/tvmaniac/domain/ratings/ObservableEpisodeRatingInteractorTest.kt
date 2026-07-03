package com.thomaskioko.tvmaniac.domain.ratings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.data.ratings.api.EpisodeRating
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ObservableEpisodeRatingInteractorTest {
    private val ratingsRepository = FakeRatingsRepository()

    private val interactor = ObservableEpisodeRatingInteractor(
        ratingsRepository = ratingsRepository,
    )

    @Test
    fun `should emit episode rating given repository has rating`() = runTest {
        val episodeRating = EpisodeRating(
            userRating = 8,
            pendingAction = PendingAction.NOTHING,
        )
        ratingsRepository.setEpisodeRating(episodeRating)

        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe episodeRating
            cancelAndConsumeRemainingEvents()
        }
    }
}
