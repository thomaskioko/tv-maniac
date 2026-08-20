package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.data.library.LibraryRemoteDataSource
import com.thomaskioko.tvmaniac.data.library.model.RemoteFollowedShow
import com.thomaskioko.tvmaniac.data.library.model.WatchlistShowIds
import com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class TraktLibraryRemoteDataSource(
    private val remoteDataSource: TraktListRemoteDataSource,
) : LibraryRemoteDataSource {

    override val provider: SyncProviderSource = SyncProviderSource.TRAKT

    override suspend fun getWatchlist(): ApiResponse<List<RemoteFollowedShow>> =
        remoteDataSource.getWatchList(sortBy = SORT_BY, sortHow = SORT_HOW)
            .map { shows -> shows.map { it.toRemoteFollowedShow() } }

    override suspend fun addToWatchlist(shows: List<WatchlistShowIds>): ApiResponse<WatchlistSyncResult> =
        remoteDataSource.addShowsToWatchList(shows.map { it.toTraktShowIds() })
            .map { WatchlistSyncResult(notFoundCount = it.notFound.shows.size) }

    override suspend fun removeFromWatchlist(shows: List<WatchlistShowIds>): ApiResponse<WatchlistSyncResult> =
        remoteDataSource.removeShowsFromWatchList(shows.map { it.toTraktShowIds() })
            .map { WatchlistSyncResult(notFoundCount = it.notFound.shows.size) }

    private companion object {
        private const val SORT_BY = "added"
        private const val SORT_HOW = "desc"
    }
}

private fun TraktFollowedShowResponse.toRemoteFollowedShow(): RemoteFollowedShow = RemoteFollowedShow(
    tmdbId = show.ids.tmdb,
    imdbId = show.ids.imdb,
    providerShowId = show.ids.trakt.toString(),
    provider = SyncProviderSource.TRAKT,
    title = show.title,
    year = show.year,
    followedAt = Instant.parse(listedAt),
)

private fun WatchlistShowIds.toTraktShowIds(): TraktShowIds = when (traktId) {
    null -> TraktShowIds(tmdbId = tmdbId)
    else -> TraktShowIds(traktId = traktId)
}
