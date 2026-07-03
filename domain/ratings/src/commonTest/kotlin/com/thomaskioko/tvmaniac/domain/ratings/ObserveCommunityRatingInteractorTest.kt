package com.thomaskioko.tvmaniac.domain.ratings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.data.ratings.api.CommunityRating
import com.thomaskioko.tvmaniac.data.ratings.api.ShowRating
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ObserveCommunityRatingInteractorTest {
    private val ratingsRepository = FakeRatingsRepository()

    private val interactor = ObserveCommunityRatingInteractor(
        ratingsRepository = ratingsRepository,
    )

    @Test
    fun `should emit community rating given show has community rating`() = runTest {
        ratingsRepository.setShowRating(
            ShowRating(userRating = null, communityRating = 8.4, communityVotes = 500L, pendingAction = PendingAction.NOTHING),
        )

        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe CommunityRating(rating = 8.4, votes = 500L)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit null given show has no community rating`() = runTest {
        ratingsRepository.setShowRating(
            ShowRating(userRating = null, communityRating = null, communityVotes = null, pendingAction = PendingAction.NOTHING),
        )

        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe null
            cancelAndConsumeRemainingEvents()
        }
    }
}
