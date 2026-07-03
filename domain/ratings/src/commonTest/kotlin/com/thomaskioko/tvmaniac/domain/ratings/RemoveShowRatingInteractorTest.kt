package com.thomaskioko.tvmaniac.domain.ratings

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RemoveShowRatingInteractorTest {
    private val ratingsRepository = FakeRatingsRepository()

    private val interactor = RemoveShowRatingInteractor(
        ratingsRepository = ratingsRepository,
    )

    @Test
    fun `should emit success given show rating is removed`() = runTest {
        interactor(84958L).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }
}
