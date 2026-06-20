package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.data.library.LibraryRemoteDataSource
import com.thomaskioko.tvmaniac.data.library.model.RemoteFollowedShow
import com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class TraktLibraryRemoteDataSource(
    private val remoteDataSource: TraktListRemoteDataSource,
) : LibraryRemoteDataSource {

    override val provider: AccountProvider = AccountProvider.TRAKT

    override suspend fun getWatchlist(): ApiResponse<List<RemoteFollowedShow>> =
        remoteDataSource.getWatchList(sortBy = SORT_BY, sortHow = SORT_HOW)
            .map { shows -> shows.map { it.toRemoteFollowedShow() } }

    override suspend fun addToWatchlist(showIds: List<Long>): ApiResponse<WatchlistSyncResult> =
        remoteDataSource.addShowsToWatchListByIds(showIds)
            .map { WatchlistSyncResult(notFoundCount = it.notFound.shows.size) }

    override suspend fun removeFromWatchlist(showIds: List<Long>): ApiResponse<WatchlistSyncResult> =
        remoteDataSource.removeShowsFromWatchListByIds(showIds)
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
    provider = AccountProvider.TRAKT,
    title = show.title,
    year = show.year,
    followedAt = Instant.parse(listedAt),
)
