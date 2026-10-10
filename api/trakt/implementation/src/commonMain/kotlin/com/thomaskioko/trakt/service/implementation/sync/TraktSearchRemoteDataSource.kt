package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.search.api.SearchRemoteDataSource
import com.thomaskioko.tvmaniac.search.api.model.RemoteSearchShow
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSearchResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class TraktSearchRemoteDataSource(
    private val remoteDataSource: TraktShowsRemoteDataSource,
) : SearchRemoteDataSource {

    override val provider: SyncProviderSource = SyncProviderSource.TRAKT

    override suspend fun searchShows(query: String, limit: Int): ApiResponse<List<RemoteSearchShow>> =
        remoteDataSource.searchShows(query = query, page = 1, limit = limit)
            .map { results -> results.mapNotNull { it.toRemoteSearchShow() } }
}

private fun TraktSearchResult.toRemoteSearchShow(): RemoteSearchShow? {
    if (type != "show") return null
    val show = show ?: return null

    return RemoteSearchShow(
        providerShowId = show.ids.trakt.toString(),
        tmdbId = show.ids.tmdb,
        title = show.title,
        year = show.year?.toInt(),
        overview = show.overview,
        status = show.status,
        episodeCount = show.airedEpisodes?.toInt(),
        rating = show.rating,
        votes = show.votes,
        genres = show.genres,
        language = show.language,
        score = score,
    )
}
