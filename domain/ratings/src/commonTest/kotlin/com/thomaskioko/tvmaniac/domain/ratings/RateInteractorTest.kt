package com.thomaskioko.tvmaniac.domain.ratings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RateInteractorTest {
    private val ratingsRepository = FakeRatingsRepository()

    private val interactor = RateInteractor(
        ratingsRepository = ratingsRepository,
    )

    @Test
    fun `should emit success given show is rated`() = runTest {
        val param = RateInteractor.Param(type = RatingEntityType.SHOW, id = 84958L, rating = 8)

        interactor(param).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }

    @Test
    fun `should emit success given season is rated`() = runTest {
        val param = RateInteractor.Param(type = RatingEntityType.SEASON, id = 1L, rating = 9)

        interactor(param).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }

    @Test
    fun `should emit success given episode is rated`() = runTest {
        val param = RateInteractor.Param(type = RatingEntityType.EPISODE, id = 1L, rating = 7)

        interactor(param).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }
}
