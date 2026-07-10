package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.startwatching.api.RemotePlanToWatchShow
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class TraktStartWatchingRemoteDataSource(
    private val remoteDataSource: TraktListRemoteDataSource,
) : StartWatchingRemoteDataSource {

    override val provider: SyncProviderSource = SyncProviderSource.TRAKT

    override suspend fun getPlanToWatch(): ApiResponse<List<RemotePlanToWatchShow>> =
        remoteDataSource.getWatchList(sortBy = SORT_BY, sortHow = SORT_HOW)
            .map { shows -> shows.map { it.toRemotePlanToWatchShow() } }

    private companion object {
        private const val SORT_BY = "added"
        private const val SORT_HOW = "desc"
    }
}

private fun TraktFollowedShowResponse.toRemotePlanToWatchShow(): RemotePlanToWatchShow =
    RemotePlanToWatchShow(
        tmdbId = show.ids.tmdb,
        imdbId = show.ids.imdb,
        providerShowId = show.ids.trakt.toString(),
        provider = SyncProviderSource.TRAKT,
        title = show.title,
        year = show.year,
        followedAt = Instant.parse(listedAt),
    )
