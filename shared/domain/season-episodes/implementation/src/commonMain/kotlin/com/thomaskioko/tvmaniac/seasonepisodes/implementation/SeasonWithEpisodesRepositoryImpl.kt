package com.thomaskioko.tvmaniac.seasonepisodes.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.datasource.cache.Season_with_episodes
import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.remote.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesCache
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SeasonWithEpisodesRepositoryImpl(
    private val apiService: TvShowsService,
    private val episodesCache: EpisodesCache,
    private val seasonsCache: SeasonsCache,
    private val seasonWithEpisodesCache: SeasonWithEpisodesCache,
    private val dispatcher: CoroutineDispatcher,
) : SeasonWithEpisodesRepository {

    override fun observeSeasonWithEpisodes(
        showId: Long
    ): Flow<Resource<List<SelectSeasonWithEpisodes>>> =
        networkBoundResource(
            query = { seasonWithEpisodesCache.observeShowEpisodes(showId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = {
                seasonsCache.getSeasonsByShowId(showId)
                    .map { apiService.getSeasonDetails(showId, it.season_number) }
            },
            saveFetchResult = { mapAndCache(showId, it) },
            onFetchFailed = {
                Logger.withTag("observeSeasonWithEpisodes").e(it) { it.message ?: "Error" }
            },
            coroutineDispatcher = dispatcher
        )

    private fun mapAndCache(showId: Long, response: List<SeasonResponse>) {
        response.forEach {

            episodesCache.insert(it.toEpisodeCacheList())

            seasonWithEpisodesCache.insert(
                Season_with_episodes(
                    show_id = showId,
                    season_id = it.id.toLong(),
                    season_number = it.season_number.toLong(),
                )
            )
        }
    }
}
