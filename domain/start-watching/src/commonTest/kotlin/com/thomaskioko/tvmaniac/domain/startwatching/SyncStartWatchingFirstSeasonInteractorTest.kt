package com.thomaskioko.tvmaniac.domain.startwatching

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsEpisodesSyncRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class SyncStartWatchingFirstSeasonInteractorTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val startWatchingRepository = FakeStartWatchingRepository()
    private val seasonsEpisodesSyncRepository = FakeSeasonsEpisodesSyncRepository()
    private val seasonsRepository = FakeSeasonsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val requestManagerRepository = FakeRequestManagerRepository(initialRequestValid = false)
    private val syncObserver = FakeSyncObserver()

    private val interactor = SyncStartWatchingFirstSeasonInteractor(
        startWatchingRepository = startWatchingRepository,
        seasonsEpisodesSyncRepository = seasonsEpisodesSyncRepository,
        seasonsRepository = seasonsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        requestManagerRepository = requestManagerRepository,
        syncObserver = syncObserver,
        dispatchers = AppCoroutineDispatchers(
            main = dispatcher,
            io = dispatcher,
            computation = dispatcher,
            databaseWrite = dispatcher,
            databaseRead = dispatcher,
        ),
        logger = FakeLogger(),
    )

    @Test
    fun `should fetch seasons for each show given cache expired`() = runTest {
        startWatchingRepository.setStartWatchingShows(shows)

        interactor.executeSync(SyncStartWatchingFirstSeasonInteractor.Param(forceRefresh = false))

        seasonsEpisodesSyncRepository.syncedShowIds shouldContainExactly listOf(1L, 2L)
        requestManagerRepository.upsertCalled shouldBe true
    }

    @Test
    fun `should skip fetch given cache still valid`() = runTest {
        requestManagerRepository.requestValid = true
        startWatchingRepository.setStartWatchingShows(shows)

        interactor.executeSync(SyncStartWatchingFirstSeasonInteractor.Param(forceRefresh = false))

        seasonsEpisodesSyncRepository.syncedShowIds.shouldBeEmpty()
    }

    @Test
    fun `should fetch given cache valid but force refresh requested`() = runTest {
        requestManagerRepository.requestValid = true
        startWatchingRepository.setStartWatchingShows(shows)

        interactor.executeSync(SyncStartWatchingFirstSeasonInteractor.Param(forceRefresh = true))

        seasonsEpisodesSyncRepository.syncedShowIds shouldContainExactly listOf(1L, 2L)
    }

    private companion object {
        val shows = listOf(
            StartWatchingShow(traktId = 1, tmdbId = 1, title = "Breaking Bad", posterPath = null, year = "2008", inLibrary = true),
            StartWatchingShow(traktId = 2, tmdbId = 2, title = "Severance", posterPath = null, year = "2022", inLibrary = true),
        )
    }
}
