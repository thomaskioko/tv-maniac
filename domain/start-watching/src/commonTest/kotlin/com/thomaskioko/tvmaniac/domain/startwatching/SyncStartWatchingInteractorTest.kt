package com.thomaskioko.tvmaniac.domain.startwatching

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository.SyncInvocation
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncStartWatchingInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val repository = FakeStartWatchingRepository()
    private val interactor = SyncStartWatchingInteractor(
        startWatchingRepository = repository,
        dispatchers = dispatchers,
    )

    @Test
    fun `should sync watchlist without force given default param`() = runTest(testDispatcher) {
        interactor.executeSync(SyncStartWatchingInteractor.Param())

        repository.syncInvocations() shouldBe listOf(SyncInvocation(forceRefresh = false))
    }

    @Test
    fun `should propagate force refresh to watchlist sync`() = runTest(testDispatcher) {
        interactor.executeSync(SyncStartWatchingInteractor.Param(forceRefresh = true))

        repository.syncInvocations() shouldBe listOf(SyncInvocation(forceRefresh = true))
    }
}
