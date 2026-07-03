package com.thomaskioko.tvmaniac.domain.ratings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.data.ratings.api.SeasonRating
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ObservableSeasonRatingInteractorTest {
    private val ratingsRepository = FakeRatingsRepository()

    private val interactor = ObservableSeasonRatingInteractor(
        ratingsRepository = ratingsRepository,
    )

    @Test
    fun `should emit season rating given repository has rating`() = runTest {
        val seasonRating = SeasonRating(
            userRating = 8,
            pendingAction = PendingAction.NOTHING,
        )
        ratingsRepository.setSeasonRating(seasonRating)

        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe seasonRating
            cancelAndConsumeRemainingEvents()
        }
    }
}
