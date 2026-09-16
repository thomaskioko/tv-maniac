package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.search.api.SearchRemoteDataSource
import com.thomaskioko.tvmaniac.search.api.model.RemoteSearchShow
import com.thomaskioko.tvmaniac.simkl.api.SimklShowsRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklSearchShowResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class SimklSearchRemoteDataSource(
    private val remoteDataSource: SimklShowsRemoteDataSource,
) : SearchRemoteDataSource {

    override val provider: SyncProviderSource = SyncProviderSource.SIMKL

    override suspend fun searchShows(query: String, limit: Int): ApiResponse<List<RemoteSearchShow>> =
        remoteDataSource.searchShows(query = query, limit = limit)
            .map { results -> results.mapNotNull { it.toRemoteSearchShow() } }
}

private fun SimklSearchShowResponse.toRemoteSearchShow(): RemoteSearchShow? {
    val simklId = ids.simklId ?: return null

    return RemoteSearchShow(
        providerShowId = simklId.toString(),
        tmdbId = ids.tmdb?.toLongOrNull(),
        title = title,
        year = year,
        overview = null,
        status = null,
        episodeCount = null,
        rating = null,
        votes = null,
        genres = null,
        language = null,
        score = null,
    )
}
