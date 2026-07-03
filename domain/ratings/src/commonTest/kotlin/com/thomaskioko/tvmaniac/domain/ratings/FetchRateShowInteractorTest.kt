package com.thomaskioko.tvmaniac.domain.ratings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class FetchRateShowInteractorTest {
    private val ratingsRepository = FakeRatingsRepository()

    private val interactor = FetchRateShowInteractor(
        ratingsRepository = ratingsRepository,
    )

    @Test
    fun `should emit success given show is rated`() = runTest {
        val param = FetchRateShowInteractor.Param(showId = 84958L, rating = 8)

        interactor(param).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }
}
