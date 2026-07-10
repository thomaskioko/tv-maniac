package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.showdetails.ShowMetadataSyncHelper
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.api.model.ShowMetadataSyncInfo
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class SyncLibraryInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val followedShowsRepository = FakeFollowedShowsRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val accountManager = FakeAccountManager().apply { setActiveProvider(SyncProviderSource.TRAKT) }

    private val interactor = SyncLibraryInteractor(
        accountManager = accountManager,
        libraryRepository = FakeLibraryRepository(),
        followedShowsRepository = followedShowsRepository,
        syncActivityInteractor = SyncActivityInteractor(
            traktActivityRepository = FakeTraktActivityRepository(),
            dispatchers = dispatchers,
        ),
        syncShowMetadataInteractor = SyncShowMetadataInteractor(
            showDetailsRepository = showDetailsRepository,
            seasonDetailsRepository = seasonDetailsRepository,
            watchProviderRepository = FakeWatchProviderRepository(),
            dispatchers = dispatchers,
        ),
        showMetadataSyncHelper = ShowMetadataSyncHelper(episodeRepository),
        watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository(),
        syncRepository = FakeActivitySyncRepository(),
        datastoreRepository = FakeDatastoreRepository(),
        dateTimeProvider = FakeDateTimeProvider(),
        dispatchers = dispatchers,
        syncObserver = FakeSyncObserver(),
        logger = FakeLogger(),
    )

    @Test
    fun `should not force refresh per-show metadata given background sync is forced`() = runTest(testDispatcher) {
        followedShowsRepository.setEntries(listOf(followedShow(showId = 7L)))

        interactor.executeSync(SyncLibraryInteractor.Param(forceRefresh = true))

        showDetailsRepository.fetchInvocations().map { it.forceRefresh } shouldBe listOf(false)
        seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(7L)
    }

    @Test
    fun `should force refresh per-show metadata given sync is user initiated`() = runTest(testDispatcher) {
        followedShowsRepository.setEntries(listOf(followedShow(showId = 7L)))

        interactor.executeSync(SyncLibraryInteractor.Param(forceRefresh = true, isUserInitiated = true))

        showDetailsRepository.fetchInvocations().map { it.forceRefresh } shouldBe listOf(true)
        seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(7L)
    }

    @Test
    fun `should skip sync given no active account`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(null)
        followedShowsRepository.setEntries(listOf(followedShow(showId = 7L)))

        interactor.executeSync(SyncLibraryInteractor.Param(forceRefresh = true, isUserInitiated = true))

        showDetailsRepository.fetchInvocations().shouldBeEmpty()
        seasonDetailsRepository.getSyncedShowIds().shouldBeEmpty()
    }

    @Test
    fun `should skip metadata fan-out for ended show with complete episode data`() = runTest(testDispatcher) {
        followedShowsRepository.setEntries(
            listOf(
                followedShow(showId = 42L),
                followedShow(showId = 99L),
            ),
        )
        episodeRepository.setShowMetadataSyncInfo(
            showId = 42L,
            info = ShowMetadataSyncInfo(status = "Ended", metadataEpisodeCount = 10, localEpisodeCount = 10),
        )
        episodeRepository.setShowMetadataSyncInfo(
            showId = 99L,
            info = ShowMetadataSyncInfo(status = "Returning Series", metadataEpisodeCount = 10, localEpisodeCount = 8),
        )

        interactor.executeSync(SyncLibraryInteractor.Param(forceRefresh = true, isUserInitiated = true))

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(99L)
        seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(99L)
    }

    private fun followedShow(showId: Long): FollowedShowEntry = FollowedShowEntry(
        showId = showId,
        followedAt = Instant.fromEpochMilliseconds(0L),
    )
}
