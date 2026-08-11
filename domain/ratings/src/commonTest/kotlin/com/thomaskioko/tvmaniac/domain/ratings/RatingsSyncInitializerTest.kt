package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RatingsSyncInitializerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val ratingsRepository = FakeRatingsRepository()

    @Test
    fun `should read the ratings the provider already holds on start`() = runTest(testDispatcher) {
        val scope = CoroutineScope(testDispatcher + Job())
        val initializer = RatingsSyncInitializer(
            coroutineScope = scope,
            ratingsRepositoryLazy = lazy { ratingsRepository },
        )

        initializer.init()
        advanceUntilIdle()

        ratingsRepository.syncUserRatingsCount shouldBe 1

        scope.cancel()
    }
}
