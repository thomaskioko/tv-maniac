package com.thomaskioko.tvmaniac.domain.ratings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.data.ratings.api.ShowRating
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ObservableShowRatingInteractorTest {
    private val ratingsRepository = FakeRatingsRepository()

    private val interactor = ObservableShowRatingInteractor(
        ratingsRepository = ratingsRepository,
    )

    @Test
    fun `should emit show rating given repository has rating`() = runTest {
        val showRating = ShowRating(
            userRating = 8,
            communityRating = 7.5,
            communityVotes = 1200L,
            pendingAction = PendingAction.NOTHING,
        )
        ratingsRepository.setShowRating(showRating)

        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe showRating
            cancelAndConsumeRemainingEvents()
        }
    }
}
