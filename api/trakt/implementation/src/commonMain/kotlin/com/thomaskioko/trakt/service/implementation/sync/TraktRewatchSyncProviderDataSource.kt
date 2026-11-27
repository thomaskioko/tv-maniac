package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchClose
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchEpisode
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchSession
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchSessionStatus
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchWrite
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchWriteResult
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSyncProviderDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktEpisodeHistoryRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncItems
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncSeason
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncSeasonEpisode
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncShow
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class TraktRewatchSyncProviderDataSource(
    private val syncRemoteDataSource: TraktSyncRemoteDataSource,
    private val episodeHistoryRemoteDataSource: TraktEpisodeHistoryRemoteDataSource,
) : RewatchSyncProviderDataSource {

    override val provider: SyncProviderSource = SyncProviderSource.TRAKT

    override suspend fun supportsRewatch(): Boolean = true

    override suspend fun readRewatchSessions(): ApiResponse<Map<Long, List<RemoteRewatchSession>>> {
        val sessions = mutableMapOf<Long, List<RemoteRewatchSession>>()
        var page = 1
        while (true) {
            val response = syncRemoteDataSource.getWatchedShows(page = page, limit = PAGE_LIMIT, extended = "progress")
            if (response !is ApiResponse.Success) return response.map { emptyMap() }

            for (show in response.body) {
                val tmdbId = show.show.ids.tmdb ?: continue
                sessions[tmdbId] = show.toRemoteRewatchSessions()
            }
            if (response.body.size < PAGE_LIMIT) return ApiResponse.Success(sessions)
            page++
        }
    }

    override suspend fun writeRewatch(write: RemoteRewatchWrite): ApiResponse<RemoteRewatchWriteResult> =
        episodeHistoryRemoteDataSource.addEpisodeWatches(
            TraktSyncItems(
                shows = listOf(
                    TraktSyncShow(
                        ids = TraktShowIds(tmdbId = write.providerShowId),
                        seasons = listOf(
                            TraktSyncSeason(
                                number = write.seasonNumber,
                                episodes = listOf(
                                    TraktSyncSeasonEpisode(
                                        number = write.episodeNumber,
                                        watchedAt = Instant.fromEpochMilliseconds(write.watchedAt).toString(),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ).map { RemoteRewatchWriteResult() }

    override suspend fun closeRewatch(close: RemoteRewatchClose): ApiResponse<Unit> = ApiResponse.Success(Unit)
}

private const val PAGE_LIMIT = 100

private fun TraktWatchedShowResponse.toRemoteRewatchSessions(): List<RemoteRewatchSession> {
    val episodes = seasons.orEmpty().flatMap { season ->
        season.episodes.mapNotNull { episode ->
            val plays = episode.plays ?: return@mapNotNull null
            if (plays <= 1L) return@mapNotNull null
            RemoteRewatchEpisode(
                seasonNumber = season.number,
                episodeNumber = episode.number,
                watchedAt = episode.lastWatchedAt?.toEpochMillis(),
                viewings = plays - 1,
            )
        }
    }

    if (episodes.isEmpty()) return emptyList()

    val watchedAt = episodes.mapNotNull { it.watchedAt }
    return listOf(
        RemoteRewatchSession(
            providerSessionId = null,
            lastWatchedAt = watchedAt.maxOrNull(),
            status = RemoteRewatchSessionStatus.CLOSED,
            startedAt = watchedAt.minOrNull(),
            episodes = episodes,
        ),
    )
}

private fun String.toEpochMillis(): Long? = runCatching { Instant.parse(this).toEpochMilliseconds() }.getOrNull()
