package com.thomaskioko.tvmaniac.domain.favorites

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.favorites.testing.FakeFavoritesRepository
import com.thomaskioko.tvmaniac.favorites.testing.FakeFavoritesRepository.SyncInvocation
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncFavoritesInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val repository = FakeFavoritesRepository()
    private val interactor = SyncFavoritesInteractor(
        favoritesRepository = repository,
        dispatchers = dispatchers,
    )

    @Test
    fun `should sync favorites given default param`() = runTest(testDispatcher) {
        interactor.executeSync(SyncFavoritesInteractor.Param())

        repository.syncInvocations() shouldBe listOf(SyncInvocation(forceRefresh = false))
    }

    @Test
    fun `should propagate force refresh to favorites sync`() = runTest(testDispatcher) {
        interactor.executeSync(SyncFavoritesInteractor.Param(forceRefresh = true))

        repository.syncInvocations() shouldBe listOf(SyncInvocation(forceRefresh = true))
    }
}
