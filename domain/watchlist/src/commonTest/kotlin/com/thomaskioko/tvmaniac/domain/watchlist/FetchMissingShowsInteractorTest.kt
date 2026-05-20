package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FetchMissingShowsInteractorTest {

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
    private val continueWatchingDao = FakeContinueWatchingDao()

    private val syncWatchedShowInteractor = SyncWatchedShowInteractor(
        showDetailsRepository = showDetailsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        dispatchers = dispatchers,
    )

    private val interactor = FetchMissingShowsInteractor(
        continueWatchingDao = continueWatchingDao,
        syncWatchedShowInteractor = syncWatchedShowInteractor,
        dispatchers = dispatchers,
    )

    @Test
    fun `should fetch each show id reported missing by the dao`() = runTest(testDispatcher) {
        continueWatchingDao.setTraktIdsMissingShowDetails(listOf(42L, 99L))

        interactor.executeSync(false)

        showDetailsRepository.fetchInvocations().map { it.id } shouldContainExactlyInAnyOrder listOf(42L, 99L)
        showDetailsRepository.fetchInvocations().all { !it.forceRefresh } shouldBe true
    }

    @Test
    fun `should pass force refresh flag to downstream sync`() = runTest(testDispatcher) {
        continueWatchingDao.setTraktIdsMissingShowDetails(listOf(42L))

        interactor.executeSync(true)

        showDetailsRepository.fetchInvocations().single().forceRefresh shouldBe true
    }

    @Test
    fun `should no-op given dao reports no missing ids`() = runTest(testDispatcher) {
        continueWatchingDao.setTraktIdsMissingShowDetails(emptyList())

        interactor.executeSync(false)

        showDetailsRepository.fetchInvocations() shouldBe emptyList()
    }
}
