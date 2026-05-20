package com.thomaskioko.tvmaniac.domain.continuewatching

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository.SyncInvocation
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncContinueWatchingInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val activityRepository = FakeTraktActivityRepository()
    private val continueWatchingRepository = FakeContinueWatchingRepository()
    private val continueWatchingDao = FakeContinueWatchingDao()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val watchProviderRepository = FakeWatchProviderRepository()

    private val syncActivityInteractor = SyncActivityInteractor(
        traktActivityRepository = activityRepository,
        dispatchers = dispatchers,
    )

    private val syncShowMetadataInteractor = SyncShowMetadataInteractor(
        showDetailsRepository = showDetailsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        watchProviderRepository = watchProviderRepository,
        dispatchers = dispatchers,
    )

    private val syncObserver = FakeSyncObserver()

    private val interactor = SyncContinueWatchingInteractor(
        syncActivityInteractor = syncActivityInteractor,
        continueWatchingRepository = continueWatchingRepository,
        continueWatchingDao = continueWatchingDao,
        syncShowMetadataInteractor = syncShowMetadataInteractor,
        syncObserver = syncObserver,
        dispatchers = dispatchers,
        logger = FakeLogger(),
    )

    @Test
    fun `should fetch activities then sync watched shows`() = runTest(testDispatcher) {
        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        activityRepository.fetchInvocations() shouldBe listOf(false)
        continueWatchingRepository.syncInvocations() shouldBe listOf(
            SyncInvocation(forceRefresh = false, useNitro = false),
        )
    }

    @Test
    fun `should propagate force refresh to watched shows sync`() = runTest(testDispatcher) {
        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = true))

        activityRepository.fetchInvocations() shouldBe listOf(true)
        continueWatchingRepository.syncInvocations() shouldBe listOf(
            SyncInvocation(forceRefresh = true, useNitro = false),
        )
    }

    @Test
    fun `should propagate useNitro to watched shows sync`() = runTest(testDispatcher) {
        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = true, useNitro = true))

        continueWatchingRepository.syncInvocations() shouldBe listOf(
            SyncInvocation(forceRefresh = true, useNitro = true),
        )
    }

    @Test
    fun `should sync metadata for every watched show`() = runTest(testDispatcher) {
        continueWatchingDao.upsert(watchedShow(traktId = 42L))
        continueWatchingDao.upsert(watchedShow(traktId = 99L))
        continueWatchingDao.upsert(watchedShow(traktId = 101L))

        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        showDetailsRepository.fetchInvocations().map { it.id } shouldContainExactlyInAnyOrder listOf(42L, 99L, 101L)
        seasonDetailsRepository.getSyncedShowIds() shouldContainExactlyInAnyOrder listOf(42L, 99L, 101L)
        watchedEpisodeSyncRepository.getSyncedShowIds() shouldContainExactlyInAnyOrder listOf(42L, 99L, 101L)
        watchProviderRepository.fetchInvocations().map { it.traktId } shouldContainExactlyInAnyOrder listOf(42L, 99L, 101L)
    }

    @Test
    fun `should propagate force refresh to per-show metadata sync`() = runTest(testDispatcher) {
        continueWatchingDao.upsert(watchedShow(traktId = 7L))

        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = true))

        showDetailsRepository.fetchInvocations().all { it.forceRefresh } shouldBe true
        watchedEpisodeSyncRepository.wasForceRefreshUsed() shouldBe true
        watchProviderRepository.fetchInvocations().all { it.forceRefresh } shouldBe true
    }

    @Test
    fun `should log SyncError to observer when per-show fetch fails`() = runTest(testDispatcher) {
        continueWatchingDao.upsert(watchedShow(traktId = 11L))
        showDetailsRepository.setFetchError(RuntimeException("rate-limited 429"))

        syncObserver.errors.test {
            interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

            val event = awaitItem()
            event.shouldBeInstanceOf<SyncError.BackgroundSyncFailed>()
            event.cause.message shouldBe "rate-limited 429"
        }
    }

    @Test
    fun `should no-op metadata fan-out when no watched shows exist`() = runTest(testDispatcher) {
        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        showDetailsRepository.fetchInvocations().shouldBeEmpty()
        seasonDetailsRepository.getSyncedShowIds().shouldBeEmpty()
        watchedEpisodeSyncRepository.getSyncedShowIds().shouldBeEmpty()
        watchProviderRepository.fetchInvocations().shouldBeEmpty()
    }

    private fun watchedShow(traktId: Long): ContinueWatchingEntry = ContinueWatchingEntry(
        traktId = traktId,
        tmdbId = traktId,
        airedEpisodes = 10L,
        completedCount = 1L,
        lastWatchedAt = NOW,
        lastUpdatedAt = NOW,
    )

    private companion object {
        private const val NOW = 1_750_000_000_000L
    }
}
