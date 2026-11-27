package com.thomaskioko.tvmaniac.domain.startwatching

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncException
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.ShowMetadataSyncHelper
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.episodes.api.model.ShowMetadataSyncInfo
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository.SyncInvocation
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncStartWatchingInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val repository = FakeStartWatchingRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val watchProviderRepository = FakeWatchProviderRepository()
    private val interactor = SyncStartWatchingInteractor(
        startWatchingRepository = repository,
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        syncShowMetadataInteractor = SyncShowMetadataInteractor(
            showDetailsRepository = showDetailsRepository,
            seasonDetailsRepository = seasonDetailsRepository,
            watchProviderRepository = watchProviderRepository,
            dispatchers = dispatchers,
        ),
        showMetadataSyncHelper = ShowMetadataSyncHelper(episodeRepository),
        dispatchers = dispatchers,
        logger = FakeLogger(),
    )

    @Test
    fun `should sync watchlist and skip watched episodes given default param`() = runTest(testDispatcher) {
        interactor.executeSync(SyncStartWatchingInteractor.Param())

        watchedEpisodeSyncRepository.syncAllInvocations().shouldBeEmpty()
        repository.syncInvocations() shouldBe listOf(SyncInvocation(forceRefresh = false))
    }

    @Test
    fun `should sync metadata for a show with no metadata given default param`() = runTest(testDispatcher) {
        repository.setStartWatchingShows(listOf(startWatchingShow(showId = 42L)))

        interactor.executeSync(SyncStartWatchingInteractor.Param())

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(42L)
        seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(42L)
        seasonDetailsRepository.getLatestSeasonRefreshShowIds().shouldBeEmpty()
    }

    @Test
    fun `should refresh latest season for a continuing show with incomplete episodes given default param`() =
        runTest(testDispatcher) {
            seasonDetailsRepository.setShowSeasonSyncExpired(true)
            repository.setStartWatchingShows(listOf(startWatchingShow(showId = 99L)))
            episodeRepository.setShowMetadataSyncInfo(
                showId = 99L,
                info = ShowMetadataSyncInfo(status = "Returning Series", metadataEpisodeCount = 10, localEpisodeCount = 8),
            )

            interactor.executeSync(SyncStartWatchingInteractor.Param())

            showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(99L)
            seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(99L)
            seasonDetailsRepository.getLatestSeasonRefreshShowIds() shouldBe listOf(99L)
        }

    @Test
    fun `should skip metadata for an ended show with complete episodes given default param`() = runTest(testDispatcher) {
        repository.setStartWatchingShows(listOf(startWatchingShow(showId = 42L), startWatchingShow(showId = 99L)))
        episodeRepository.setShowMetadataSyncInfo(
            showId = 42L,
            info = ShowMetadataSyncInfo(status = "Ended", metadataEpisodeCount = 10, localEpisodeCount = 10),
        )
        episodeRepository.setShowMetadataSyncInfo(
            showId = 99L,
            info = ShowMetadataSyncInfo(status = "Returning Series", metadataEpisodeCount = 10, localEpisodeCount = 8),
        )

        interactor.executeSync(SyncStartWatchingInteractor.Param())

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(99L)
        seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(99L)
    }

    @Test
    fun `should sync watchlist and watched episodes with force refresh given force refresh param`() = runTest(testDispatcher) {
        interactor.executeSync(SyncStartWatchingInteractor.Param(forceRefresh = true))

        watchedEpisodeSyncRepository.syncAllInvocations() shouldBe listOf(true)
        repository.syncInvocations() shouldBe listOf(SyncInvocation(forceRefresh = true))
    }

    @Test
    fun `should sync metadata only for shows that need it given force refresh param`() = runTest(testDispatcher) {
        repository.setStartWatchingShows(listOf(startWatchingShow(showId = 42L), startWatchingShow(showId = 99L)))
        episodeRepository.setShowMetadataSyncInfo(
            showId = 42L,
            info = ShowMetadataSyncInfo(status = "Ended", metadataEpisodeCount = 10, localEpisodeCount = 10),
        )

        interactor.executeSync(SyncStartWatchingInteractor.Param(forceRefresh = true))

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(99L)
        seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(99L)
        watchProviderRepository.fetchInvocations().shouldBeEmpty()
    }

    @Test
    fun `should continue with the next show given a show metadata sync fails`() = runTest(testDispatcher) {
        repository.setStartWatchingShows(listOf(startWatchingShow(showId = 42L), startWatchingShow(showId = 99L)))
        showDetailsRepository.setFetchError(IllegalStateException("boom"))

        interactor.executeSync(SyncStartWatchingInteractor.Param())

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(42L, 99L)
    }

    @Test
    fun `should stop the metadata pass given a retryable failure`() = runTest(testDispatcher) {
        repository.setStartWatchingShows(listOf(startWatchingShow(showId = 42L), startWatchingShow(showId = 99L)))
        showDetailsRepository.setFetchError(SyncException(SyncError.Retryable.RateLimited()))

        interactor.executeSync(SyncStartWatchingInteractor.Param())

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(42L)
    }

    @Test
    fun `should propagate cancellation given the metadata pass is cancelled`() = runTest(testDispatcher) {
        repository.setStartWatchingShows(listOf(startWatchingShow(showId = 42L)))
        showDetailsRepository.setFetchError(CancellationException("cancelled"))

        shouldThrow<CancellationException> {
            interactor.executeSync(SyncStartWatchingInteractor.Param())
        }
    }

    private fun startWatchingShow(showId: Long): StartWatchingShow = StartWatchingShow(
        showId = showId,
        tmdbId = showId,
        title = "Show $showId",
        posterPath = null,
        year = null,
        inLibrary = true,
    )
}
