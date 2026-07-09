package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository.SyncInvocation
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.ShowMetadataSyncHelper
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.api.model.ShowMetadataSyncInfo
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
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
    private val episodeRepository = FakeEpisodeRepository()
    private val accountManager = FakeAccountManager().apply { setActiveProvider(AccountProvider.TRAKT) }

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

    private fun buildInteractor(supportsContinueWatchingFetch: Boolean = true) = SyncContinueWatchingInteractor(
        accountManager = accountManager,
        syncActivityInteractor = syncActivityInteractor,
        continueWatchingRepository = continueWatchingRepository,
        syncShowMetadataInteractor = syncShowMetadataInteractor,
        showMetadataSyncHelper = ShowMetadataSyncHelper(episodeRepository),
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        activeProviderFeatures = { FakeProviderFeatures(supportsContinueWatchingFetch = supportsContinueWatchingFetch) },
        requestManagerRepository = requestManagerRepository,
        dispatchers = dispatchers,
        logger = FakeLogger(),
    )

    private val interactor = buildInteractor()

    @Test
    fun `should skip when ttl valid and not force refresh`() = runTest(testDispatcher) {
        requestManagerRepository.requestValid = true

        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        activityRepository.fetchInvocations().shouldBeEmpty()
        continueWatchingRepository.syncInvocations().shouldBeEmpty()
        watchedEpisodeSyncRepository.syncAllInvocations().shouldBeEmpty()
    }

    @Test
    fun `should skip sync given no active account`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(null)
        continueWatchingRepository.setEntries(listOf(watchedShow(showId = 7L)))

        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = true))

        activityRepository.fetchInvocations().shouldBeEmpty()
        continueWatchingRepository.syncInvocations().shouldBeEmpty()
        watchedEpisodeSyncRepository.syncAllInvocations().shouldBeEmpty()
        showDetailsRepository.fetchInvocations().shouldBeEmpty()
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
    fun `should sync show and season details for every watched show without fetching providers`() = runTest(testDispatcher) {
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
        watchProviderRepository.fetchInvocations().shouldBeEmpty()
    }

    @Test
    fun `should not force refresh per-show metadata even when the sync is forced`() = runTest(testDispatcher) {
        continueWatchingRepository.setEntries(listOf(watchedShow(showId = 7L)))

        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = true))

        showDetailsRepository.fetchInvocations().map { it.forceRefresh } shouldBe listOf(false)
        seasonDetailsRepository.getSyncedShowIds() shouldContainExactlyInAnyOrder listOf(7L)
        watchProviderRepository.fetchInvocations().shouldBeEmpty()
    }

    @Test
    fun `should skip metadata fan-out for ended show with complete episode data`() = runTest(testDispatcher) {
        continueWatchingRepository.setEntries(
            listOf(
                watchedShow(showId = 42L),
                watchedShow(showId = 99L),
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

        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(99L)
        seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(99L)
    }

    @Test
    fun `should no-op metadata fan-out when no watched shows exist`() = runTest(testDispatcher) {
        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        showDetailsRepository.fetchInvocations().shouldBeEmpty()
        seasonDetailsRepository.getSyncedShowIds().shouldBeEmpty()
        watchProviderRepository.fetchInvocations().shouldBeEmpty()
    }

    @Test
    fun `should derive continue watching membership given continue watching fetch is unsupported`() = runTest(testDispatcher) {
        buildInteractor(supportsContinueWatchingFetch = false)
            .executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        continueWatchingRepository.deriveMembershipInvocationCount() shouldBe 1
    }

    @Test
    fun `should not derive continue watching membership given continue watching fetch is supported`() = runTest(testDispatcher) {
        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        continueWatchingRepository.deriveMembershipInvocationCount() shouldBe 0
    }

    @Test
    fun `should skip continue watching sync given continue watching fetch is unsupported`() = runTest(testDispatcher) {
        buildInteractor(supportsContinueWatchingFetch = false)
            .executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        continueWatchingRepository.syncInvocations().shouldBeEmpty()
        watchedEpisodeSyncRepository.syncAllInvocations() shouldBe listOf(false)
        continueWatchingRepository.deriveMembershipInvocationCount() shouldBe 1
    }

    @Test
    fun `should invoke continue watching sync given continue watching fetch is supported`() = runTest(testDispatcher) {
        interactor.executeSync(SyncContinueWatchingInteractor.Param(forceRefresh = false))

        continueWatchingRepository.syncInvocations() shouldBe listOf(
            SyncInvocation(forceRefresh = false, useNitro = false),
        )
        watchedEpisodeSyncRepository.syncAllInvocations() shouldBe listOf(false)
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
