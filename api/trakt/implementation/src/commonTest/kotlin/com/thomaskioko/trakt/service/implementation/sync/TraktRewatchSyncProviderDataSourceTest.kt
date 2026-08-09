package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchWrite
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedSeasonResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktEpisodeHistoryRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktSyncRemoteDataSource
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

internal class TraktRewatchSyncProviderDataSourceTest {

    private val syncRemoteDataSource = FakeTraktSyncRemoteDataSource()
    private val episodeHistoryRemoteDataSource = FakeTraktEpisodeHistoryRemoteDataSource()
    private val source = TraktRewatchSyncProviderDataSource(
        syncRemoteDataSource = syncRemoteDataSource,
        episodeHistoryRemoteDataSource = episodeHistoryRemoteDataSource,
    )

    @Test
    fun `should report trakt as its provider`() {
        source.provider shouldBe SyncProviderSource.TRAKT
    }

    @Test
    fun `should always support rewatch`() = runTest {
        source.supportsRewatch() shouldBe true
    }

    @Test
    fun `should map an episode with more than one play into extra sessions of one`() = runTest {
        syncRemoteDataSource.setWatchedShows(ApiResponse.Success(listOf(showWithPlays(tmdbId = 1396L, plays = 3L))))

        val sessions = source.readRewatchSessions(providerShowId = 1396L).getOrThrow()

        sessions shouldHaveSize 1
        sessions.first().seasonNumber shouldBe 1L
        sessions.first().episodeNumber shouldBe 1L
        sessions.first().episodeCount shouldBe 2L
        sessions.first().providerSessionId shouldBe null
    }

    @Test
    fun `should request the progress extended payload given rewatch sessions are read`() = runTest {
        syncRemoteDataSource.setWatchedShows(ApiResponse.Success(listOf(showWithPlays(tmdbId = 1396L, plays = 3L))))

        source.readRewatchSessions(providerShowId = 1396L)

        syncRemoteDataSource.watchedShowsExtended(1) shouldBe "progress"
    }

    @Test
    fun `should return an empty list given the episode was only played once`() = runTest {
        syncRemoteDataSource.setWatchedShows(ApiResponse.Success(listOf(showWithPlays(tmdbId = 1396L, plays = 1L))))

        val sessions = source.readRewatchSessions(providerShowId = 1396L).getOrThrow()

        sessions.shouldBeEmpty()
    }

    @Test
    fun `should return an empty list given no show matches the requested tmdb id`() = runTest {
        syncRemoteDataSource.setWatchedShows(ApiResponse.Success(listOf(showWithPlays(tmdbId = 1396L, plays = 3L))))

        val sessions = source.readRewatchSessions(providerShowId = 555L).getOrThrow()

        sessions.shouldBeEmpty()
    }

    @Test
    fun `should preserve unauthenticated given watched shows has no session`() = runTest {
        syncRemoteDataSource.setWatchedShows(ApiResponse.Unauthenticated)

        val result = source.readRewatchSessions(providerShowId = 1396L)

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }

    @Test
    fun `should preserve the http error given watched shows fails`() = runTest {
        syncRemoteDataSource.setWatchedShows(ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"))

        val result = source.readRewatchSessions(providerShowId = 1396L)

        result.shouldBeInstanceOf<ApiResponse.Error.HttpError<*>>()
    }

    @Test
    fun `should post the episode through the history endpoint given a rewatch is written`() = runTest {
        val result = source.writeRewatch(
            RemoteRewatchWrite(
                localSessionId = 42L,
                providerShowId = 1396L,
                providerSessionId = null,
                seasonNumber = 1L,
                episodeNumber = 5L,
                watchedAt = 1_750_000_000_000L,
            ),
        )

        result.shouldBeInstanceOf<ApiResponse.Success<*>>()
        val submittedShow = checkNotNull(episodeHistoryRemoteDataSource.lastAddedItems).shows.orEmpty().first()
        submittedShow.ids.tmdbId shouldBe 1396L
        val submittedSeason = submittedShow.seasons.first()
        submittedSeason.number shouldBe 1L
        val submittedEpisode = submittedSeason.episodes.first()
        submittedEpisode.number shouldBe 5L
        submittedEpisode.watchedAt shouldBe Instant.fromEpochMilliseconds(1_750_000_000_000L).toString()
    }

    @Test
    fun `should preserve the http error given the history write fails`() = runTest {
        episodeHistoryRemoteDataSource.setAddResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"),
        )

        val result = source.writeRewatch(
            RemoteRewatchWrite(
                localSessionId = 42L,
                providerShowId = 1396L,
                providerSessionId = null,
                seasonNumber = 1L,
                episodeNumber = 5L,
                watchedAt = 1_750_000_000_000L,
            ),
        )

        result.shouldBeInstanceOf<ApiResponse.Error.HttpError<*>>()
    }

    private fun showWithPlays(tmdbId: Long, plays: Long): TraktWatchedShowResponse = TraktWatchedShowResponse(
        plays = plays,
        show = TraktShowResponse(
            title = "Breaking Bad",
            ids = ShowIds(trakt = 1388L, tmdb = tmdbId),
        ),
        seasons = listOf(
            TraktWatchedSeasonResponse(
                number = 1,
                episodes = listOf(
                    TraktWatchedEpisodeResponse(number = 1, plays = plays, lastWatchedAt = "2026-04-26T22:15:42.000Z"),
                ),
            ),
        ),
    )
}
