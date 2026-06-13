package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository.SyncInvocation
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
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
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val watchProviderRepository = FakeWatchProviderRepository()
    private val requestManagerRepository = FakeRequestManagerRepository(initialRequestValid = false)
    private val accountManager = FakeAccountManager()

    private val syncActivityInteractor = SyncActivityInteractor(
        traktActivityRepository = activityRepository,
        dispatchers = dispatchers,
    )

    private val syncShowMetadataInteractor = SyncShowMetadataInteractor(
        showDetailsRepository = showDetailsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        watchProviderRepository = watchProviderRepository,
        dispatchers = dispatchers,
    )

    private val interactor = SyncContinueWatchingInteractor(
        syncActivityInteractor = syncActivityInteractor,
        continueWatchingRepository = continueWatchingRepository,
        syncShowMetadataInteractor = syncShowMetadataInteractor,
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        accountManager = accountManager,
        requestManagerRepository = requestManagerRepository,
        dispatchers = dispatchers,
        logger = FakeLogger(),
    )

    @Test
    fun `should skip when ttl valid and not force refresh`() = runTest(testDispatcher) {
        requestManagerRepository.requestValid = true

        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        activityRepository.fetchInvocations().shouldBeEmpty()
        continueWatchingRepository.syncInvocations().shouldBeEmpty()
        watchedEpisodeSyncRepository.syncAllInvocations().shouldBeEmpty()
    }

    @Test
    fun `should fetch activities and bulk watched episodes then sync continue watching`() =
        runTest(testDispatcher) {
            interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

            activityRepository.fetchInvocations() shouldBe listOf(false)
            watchedEpisodeSyncRepository.syncAllInvocations() shouldBe listOf(false)
            continueWatchingRepository.syncInvocations() shouldBe listOf(
                SyncInvocation(forceRefresh = false, useNitro = false),
            )
        }

    @Test
    fun `should propagate force refresh to bulk and continue watching syncs`() =
        runTest(testDispatcher) {
            requestManagerRepository.requestValid = true

            interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = true))

            activityRepository.fetchInvocations() shouldBe listOf(true)
            watchedEpisodeSyncRepository.syncAllInvocations() shouldBe listOf(true)
            continueWatchingRepository.syncInvocations() shouldBe listOf(
                SyncInvocation(forceRefresh = true, useNitro = false),
            )
        }

    @Test
    fun `should propagate useNitro to continue watching sync`() = runTest(testDispatcher) {
        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = true, useNitro = true))

        continueWatchingRepository.syncInvocations() shouldBe listOf(
            SyncInvocation(forceRefresh = true, useNitro = true),
        )
    }

    @Test
    fun `should sync metadata for every watched show`() = runTest(testDispatcher) {
        continueWatchingRepository.setEntries(
            listOf(
                watchedShow(showId = 42L),
                watchedShow(showId = 99L),
                watchedShow(showId = 101L),
            ),
        )

        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        showDetailsRepository.fetchInvocations().map { it.id } shouldContainExactlyInAnyOrder listOf(42L, 99L, 101L)
        seasonDetailsRepository.getSyncedShowIds() shouldContainExactlyInAnyOrder listOf(42L, 99L, 101L)
        watchProviderRepository.fetchInvocations().map { it.showId } shouldContainExactlyInAnyOrder listOf(42L, 99L, 101L)
    }

    @Test
    fun `should propagate force refresh to per-show metadata sync`() = runTest(testDispatcher) {
        continueWatchingRepository.setEntries(listOf(watchedShow(showId = 7L)))

        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = true))

        showDetailsRepository.fetchInvocations().all { it.forceRefresh } shouldBe true
        watchProviderRepository.fetchInvocations().all { it.forceRefresh } shouldBe true
    }

    @Test
    fun `should no-op metadata fan-out when no watched shows exist`() = runTest(testDispatcher) {
        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        showDetailsRepository.fetchInvocations().shouldBeEmpty()
        seasonDetailsRepository.getSyncedShowIds().shouldBeEmpty()
        watchProviderRepository.fetchInvocations().shouldBeEmpty()
    }

    @Test
    fun `should derive continue watching membership when active provider is simkl`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(AccountProvider.SIMKL)

        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        continueWatchingRepository.deriveMembershipInvocationCount() shouldBe 1
    }

    @Test
    fun `should not derive continue watching membership when active provider is trakt`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(AccountProvider.TRAKT)

        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        continueWatchingRepository.deriveMembershipInvocationCount() shouldBe 0
    }

    private fun watchedShow(showId: Long): ContinueWatchingEntry = ContinueWatchingEntry(
        showId = showId,
        tmdbId = showId,
        airedEpisodes = 10L,
        completedCount = 1L,
        lastWatchedAt = NOW,
        lastUpdatedAt = NOW,
    )

    private companion object {
        private const val NOW = 1_750_000_000_000L
    }
}
