package com.thomaskioko.tvmaniac.domain.rewatch

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.data.rewatch.testing.FakeRewatchRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncRewatchInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val rewatchRepository = FakeRewatchRepository()

    private val interactor = SyncRewatchInteractor(
        rewatchRepository = rewatchRepository,
        dispatchers = dispatchers,
    )

    @Test
    fun `should emit success given rewatch sessions are synced`() = runTest(testDispatcher) {
        val param = SyncRewatchInteractor.Param(showId = SHOW_ID)

        interactor(param).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }
    }

    @Test
    fun `should let cancellation through given the sync is cancelled`() = runTest(testDispatcher) {
        rewatchRepository.setSyncException(CancellationException("cancelled"))

        shouldThrow<CancellationException> {
            interactor.executeSync(SyncRewatchInteractor.Param(showId = SHOW_ID))
        }
    }

    private companion object {
        private const val SHOW_ID = 84958L
    }
}
