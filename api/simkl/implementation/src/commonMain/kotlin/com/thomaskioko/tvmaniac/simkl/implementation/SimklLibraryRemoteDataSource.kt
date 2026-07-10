package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.data.library.LibraryRemoteDataSource
import com.thomaskioko.tvmaniac.data.library.model.RemoteFollowedShow
import com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult
import com.thomaskioko.tvmaniac.simkl.api.SimklSyncRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedShow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class SimklLibraryRemoteDataSource(
    private val syncRemoteDataSource: SimklSyncRemoteDataSource,
) : LibraryRemoteDataSource {

    override val provider: SyncProviderSource = SyncProviderSource.SIMKL

    override suspend fun getWatchlist(): ApiResponse<List<RemoteFollowedShow>> =
        syncRemoteDataSource.getAllWatchedShows()
            .map { response ->
                response.shows
                    .filter { it.status in TRACKED_STATUSES }
                    .map { it.toRemoteFollowedShow() }
            }

    override suspend fun addToWatchlist(showIds: List<Long>): ApiResponse<WatchlistSyncResult> =
        ApiResponse.Success(WatchlistSyncResult(notFoundCount = 0))

    override suspend fun removeFromWatchlist(showIds: List<Long>): ApiResponse<WatchlistSyncResult> =
        ApiResponse.Success(WatchlistSyncResult(notFoundCount = 0))

    private companion object {
        private val TRACKED_STATUSES = setOf("plantowatch", "watching", "hold")
    }
}

private fun SimklWatchedShow.toRemoteFollowedShow(): RemoteFollowedShow = RemoteFollowedShow(
    tmdbId = show.ids.tmdb?.toLongOrNull(),
    imdbId = show.ids.imdb,
    providerShowId = show.ids.simkl?.toString(),
    provider = SyncProviderSource.SIMKL,
    title = show.title ?: "",
    year = show.year,
    followedAt = lastWatchedAt?.let { runCatching { Instant.parse(it) }.getOrNull() }
        ?: Instant.fromEpochSeconds(0),
)
