package com.thomaskioko.tvmaniac.domain.ratings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RefreshCommunityRatingInteractorTest {
    private val ratingsRepository = FakeRatingsRepository()

    private val interactor = RefreshCommunityRatingInteractor(
        ratingsRepository = ratingsRepository,
    )

    @Test
    fun `should emit success given community rating is refreshed`() = runTest {
        interactor(RefreshCommunityRatingInteractor.Param(showId = 84958L)).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }
}
