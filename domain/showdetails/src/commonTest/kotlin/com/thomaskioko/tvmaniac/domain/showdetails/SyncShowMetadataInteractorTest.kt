package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncShowMetadataInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val watchProviderRepository = FakeWatchProviderRepository()

    private val interactor = SyncShowMetadataInteractor(
        showDetailsRepository = showDetailsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        watchProviderRepository = watchProviderRepository,
        dispatchers = dispatchers,
    )

    @Test
    fun `should fan out show details season details and watch providers for the given trakt id`() =
        runTest(testDispatcher) {
            interactor.executeSync(SyncShowMetadataInteractor.Param(traktId = 1388L))

            showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(1388L)
            seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(1388L)
            watchProviderRepository.fetchInvocations().map { it.traktId } shouldBe listOf(1388L)
        }

    @Test
    fun `should propagate force refresh to every downstream repository`() = runTest(testDispatcher) {
        interactor.executeSync(
            SyncShowMetadataInteractor.Param(traktId = 1388L, forceRefresh = true),
        )

        showDetailsRepository.fetchInvocations().single().forceRefresh shouldBe true
        watchProviderRepository.fetchInvocations().single().forceRefresh shouldBe true
    }
}
