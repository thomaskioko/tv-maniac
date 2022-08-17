package com.thomaskioko.tvmaniac.episodes.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class EpisodeRepositoryImpl(
    private val apiService: TmdbService,
    private val episodesCache: EpisodesCache,
    private val dispatcher: CoroutineDispatcher,
) : EpisodeRepository {

    override fun observeSeasonEpisodes(
        tvShowId: Long,
        seasonId: Long,
        seasonNumber: Long
    ): Flow<Resource<List<EpisodesBySeasonId>>> = networkBoundResource(
        query = { episodesCache.observeEpisode(seasonId) },
        shouldFetch = { it.isNullOrEmpty() },
        fetch = { apiService.getSeasonDetails(tvShowId, seasonNumber) },
        saveFetchResult = { episodesCache.insert(it.toEpisodeCacheList()) },
        onFetchFailed = { Logger.withTag("observeSeasonEpisodes").e(it.resolveError()) },
        coroutineDispatcher = dispatcher
    )
}
