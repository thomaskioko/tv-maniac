package com.thomaskioko.tvmaniac.episodes.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.util.getErrorMessage
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class EpisodeRepositoryImpl(
    private val apiService: TvShowsService,
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
        onFetchFailed = { Logger.withTag("observeSeasonEpisodes").e(it.getErrorMessage()) },
        coroutineDispatcher = dispatcher
    )
}
