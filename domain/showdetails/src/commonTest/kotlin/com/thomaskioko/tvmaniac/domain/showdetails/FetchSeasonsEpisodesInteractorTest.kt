package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FetchSeasonsEpisodesInteractorTest {

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
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()

    private val interactor = FetchSeasonsEpisodesInteractor(
        showDetailsRepository = showDetailsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        dispatchers = dispatchers,
    )

    @Test
    fun `should fetch show details then season details then watch status for the given show`() =
        runTest(testDispatcher) {
            interactor.executeSync(FetchSeasonsEpisodesInteractor.Param(id = 1388L))

            showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(1388L)
            seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(1388L)
            watchedEpisodeSyncRepository.getSyncedShowIds() shouldBe listOf(1388L)
        }

    @Test
    fun `should propagate force refresh to every downstream repository`() = runTest(testDispatcher) {
        interactor.executeSync(FetchSeasonsEpisodesInteractor.Param(id = 1388L, forceRefresh = true))

        showDetailsRepository.fetchInvocations().single().forceRefresh shouldBe true
        watchedEpisodeSyncRepository.wasForceRefreshUsed() shouldBe true
    }
}
