package com.thomaskioko.tvmaniac.domain.notifications.interactor

import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowMetadataSyncInfo
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.UpcomingEpisode
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration

class SyncCalendarInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val dateTimeProvider = FakeDateTimeProvider()

    private fun buildInteractor(
        episodeRepository: EpisodeRepository,
        supportsCalendar: Boolean,
    ) = SyncCalendarInteractor(
        episodeRepository = episodeRepository,
        dateTimeProvider = dateTimeProvider,
        activeProviderFeatures = { FakeProviderFeatures(supportsCalendar = supportsCalendar) },
        logger = FakeLogger(),
        dispatchers = dispatchers,
    )

    @Test
    fun `should skip calendar sync given provider does not support calendar`() = runTest(testDispatcher) {
        val episodeRepository = CountingCalendarEpisodeRepository()

        buildInteractor(episodeRepository = episodeRepository, supportsCalendar = false)
            .executeSync(SyncCalendarInteractor.Params())

        episodeRepository.syncUpcomingCount shouldBe 0
    }

    @Test
    fun `should run calendar sync given provider supports calendar`() = runTest(testDispatcher) {
        val episodeRepository = CountingCalendarEpisodeRepository()

        buildInteractor(episodeRepository = episodeRepository, supportsCalendar = true)
            .executeSync(SyncCalendarInteractor.Params())

        episodeRepository.syncUpcomingCount shouldBe 1
    }

    @Test
    fun `should pass forceRefresh parameter to episode repository given forceRefresh is true`() = runTest(testDispatcher) {
        val episodeRepository = CountingCalendarEpisodeRepository()

        buildInteractor(episodeRepository = episodeRepository, supportsCalendar = true)
            .executeSync(SyncCalendarInteractor.Params(days = 14, forceRefresh = true))

        episodeRepository.syncUpcomingCount shouldBe 1
        episodeRepository.lastForceRefresh shouldBe true
        episodeRepository.lastDays shouldBe 14
    }
}

private class CountingCalendarEpisodeRepository : EpisodeRepository {
    var syncUpcomingCount: Int = 0
    var lastForceRefresh: Boolean = false
    var lastDays: Int = 0

    override fun observeEpisodeById(episodeId: Long): Flow<EpisodeById?> = flowOf(null)
    override fun observeRecentlyWatched(limit: Long): Flow<List<RecentlyWatchedEpisode>> = flowOf(emptyList())
    override suspend fun markEpisodeAsWatched(showId: Long, episodeId: Long, seasonNumber: Long, episodeNumber: Long) {}
    override suspend fun markEpisodeAndPreviousEpisodesWatched(showId: Long, episodeId: Long, seasonNumber: Long, episodeNumber: Long) {}
    override suspend fun markEpisodeAsUnwatched(showId: Long, episodeId: Long) {}
    override fun observeSeasonWatchProgress(showId: Long, seasonNumber: Long): Flow<SeasonWatchProgress> =
        flowOf(SeasonWatchProgress(0, 0, 0, 0))
    override fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress> =
        flowOf(ShowWatchProgress(0, 0, 0))
    override fun observeAllSeasonsWatchProgress(showId: Long): Flow<List<SeasonWatchProgress>> = flowOf(emptyList())
    override suspend fun markSeasonWatched(showId: Long, seasonNumber: Long) {}
    override suspend fun markSeasonAndPreviousSeasonsWatched(showId: Long, seasonNumber: Long) {}
    override suspend fun markSeasonUnwatched(showId: Long, seasonNumber: Long) {}
    override fun observeUnwatchedCountInPreviousSeasons(showId: Long, seasonNumber: Long): Flow<Long> = flowOf(0L)
    override suspend fun getUpcomingEpisodesFromFollowedShows(limit: Duration): List<UpcomingEpisode> = emptyList()
    override suspend fun getShowMetadataSyncInfo(showId: Long): ShowMetadataSyncInfo? = null
    override suspend fun syncUpcomingEpisodes(startDate: String, days: Int, forceRefresh: Boolean) {
        syncUpcomingCount++
        lastForceRefresh = forceRefresh
        lastDays = days
    }
}
