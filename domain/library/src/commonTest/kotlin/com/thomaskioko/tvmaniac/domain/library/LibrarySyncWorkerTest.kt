package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.showdetails.ShowMetadataSyncHelper
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibrarySyncWorkerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val accountManager = FakeAccountManager().apply { setActiveProvider(SyncProviderSource.TRAKT) }
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val logger = FakeLogger()

    private val interactor = SyncLibraryInteractor(
        accountManager = accountManager,
        libraryRepository = FakeLibraryRepository(),
        followedShowsRepository = FakeFollowedShowsRepository(),
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
        showMetadataSyncHelper = ShowMetadataSyncHelper(FakeEpisodeRepository()),
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        showDetailsRepository = showDetailsRepository,
        syncRepository = FakeActivitySyncRepository(),
        datastoreRepository = FakeDatastoreRepository(),
        dateTimeProvider = FakeDateTimeProvider(),
        dispatchers = dispatchers,
        syncObserver = FakeSyncObserver(),
        logger = logger,
    )

    private val worker = LibrarySyncWorker(
        syncLibraryInteractor = lazyOf(interactor),
        accountManager = lazyOf(accountManager),
        logger = logger,
    )

    @Test
    fun `should return success given the sync completes`() = runTest(testDispatcher) {
        worker.doWork() shouldBe WorkerResult.Success
    }

    @Test
    fun `should return a failure carrying the cause given the sync throws`() = runTest(testDispatcher) {
        val cause = RuntimeException("network down")
        watchedEpisodeSyncRepository.setSyncAllError(cause)

        val result = worker.doWork()

        result.shouldBeInstanceOf<WorkerResult.Failure>()
        (result as WorkerResult.Failure).cause shouldBe cause
    }

    @Test
    fun `should not log the failure itself given the sync throws`() = runTest(testDispatcher) {
        watchedEpisodeSyncRepository.setSyncAllError(RuntimeException("network down"))

        worker.doWork()

        logger.recordedErrors.size shouldBe 0
        logger.breadcrumbs.size shouldBe 0
    }
}
