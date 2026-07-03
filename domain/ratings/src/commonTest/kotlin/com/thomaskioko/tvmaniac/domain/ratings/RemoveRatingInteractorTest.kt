package com.thomaskioko.tvmaniac.domain.ratings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RemoveRatingInteractorTest {
    private val ratingsRepository = FakeRatingsRepository()

    private val interactor = RemoveRatingInteractor(
        ratingsRepository = ratingsRepository,
    )

    @Test
    fun `should emit success given show rating is removed`() = runTest {
        val param = RemoveRatingInteractor.Param(type = RatingEntityType.SHOW, id = 84958L)

        interactor(param).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }

    @Test
    fun `should emit success given season rating is removed`() = runTest {
        val param = RemoveRatingInteractor.Param(type = RatingEntityType.SEASON, id = 1L)

        interactor(param).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }

    @Test
    fun `should emit success given episode rating is removed`() = runTest {
        val param = RemoveRatingInteractor.Param(type = RatingEntityType.EPISODE, id = 1L)

        interactor(param).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }
}
