package com.thomaskioko.tvmaniac.datasource.repository.episode

import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCache
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.mapper.toEpisodeCacheList
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.remote.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.remote.util.getErrorMessage
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import com.thomaskioko.tvmaniac.shared.core.util.networkBoundResource
import com.thomaskioko.tvmaniac.util.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class EpisodeRepositoryImpl(
    private val apiService: TvShowsService,
    private val episodesCache: EpisodesCache,
    private val seasonCache: SeasonsCache,
    private val dispatcher: CoroutineDispatcher,
) : EpisodeRepository {

    override fun observeSeasonEpisodes(
        tvShowId: Int,
        seasonId: Int,
        seasonNumber: Int
    ): Flow<Resource<List<EpisodesBySeasonId>>> = networkBoundResource(
        query = { episodesCache.observeEpisode(seasonId) },
        shouldFetch = { it.isNullOrEmpty() },
        fetch = { apiService.getSeasonDetails(tvShowId, seasonNumber) },
        saveFetchResult = { mapAndCache(it, seasonId) },
        onFetchFailed = { Logger("observeSeasonEpisodes").log(it.getErrorMessage()) },
        coroutineDispatcher = dispatcher
    )

    private fun mapAndCache(
        response: SeasonResponse,
        seasonId: Int
    ) {
        val episodeEntityList = response.toEpisodeCacheList()

        // Insert episodes
        episodesCache.insert(episodeEntityList)

        val episodesIds = mutableListOf<Int>()
        for (episode in episodeEntityList) {
            episodesIds.add(episode.id.toInt())
        }

        // Update season episode list
        seasonCache.updateSeasonEpisodesIds(seasonId = seasonId, episodeIds = episodesIds)
    }
}
