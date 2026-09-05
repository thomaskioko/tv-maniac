package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.ShowMetadataSyncHelper
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlag
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContinueWatchingSyncWorkerTest {

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
    private val logger = FakeLogger()

    private val interactor = SyncContinueWatchingInteractor(
        accountManager = accountManager,
        syncActivityInteractor = SyncActivityInteractor(
            traktActivityRepository = FakeTraktActivityRepository(),
            dispatchers = dispatchers,
        ),
        continueWatchingRepository = FakeContinueWatchingRepository(),
        syncShowMetadataInteractor = SyncShowMetadataInteractor(
            showDetailsRepository = FakeShowDetailsRepository(),
            seasonDetailsRepository = FakeSeasonDetailsRepository(),
            watchProviderRepository = FakeWatchProviderRepository(),
            dispatchers = dispatchers,
        ),
        showMetadataSyncHelper = ShowMetadataSyncHelper(FakeEpisodeRepository()),
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        activeProviderFeatures = { FakeProviderFeatures(supportsContinueWatchingFetch = true) },
        requestManagerRepository = FakeRequestManagerRepository(initialRequestValid = false),
        dispatchers = dispatchers,
        logger = logger,
    )

    private val worker = ContinueWatchingSyncWorker(
        syncContinueWatchingInteractor = lazyOf(interactor),
        accountManager = lazyOf(accountManager),
        nitroFlag = FakeFeatureFlag(initial = false),
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
