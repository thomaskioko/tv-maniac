package com.thomaskioko.tvmaniac.seasondetails.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Season_with_episodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonsByShowId
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsCache
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SeasonDetailsRepositoryImpl(
    private val traktService: TraktService,
    private val seasonCache: SeasonsCache,
    private val episodesCache: EpisodesCache,
    private val dispatcher: CoroutineDispatcher,
) : SeasonDetailsRepository {

    override fun observeShowSeasons(traktId: Int): Flow<Resource<List<SelectSeasonsByShowId>>> =
        networkBoundResource(
            query = { seasonCache.observeSeasons(traktId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { traktService.getShowSeasons(traktId) },
            saveFetchResult = { seasonCache.insertSeasons(it.toSeasonCacheList(traktId)) },
            onFetchFailed = { Logger.withTag("observeShowSeasons").e(it.resolveError()) },
            coroutineDispatcher = dispatcher
        )

    override fun updateSeasonEpisodes(showId: Int): Flow<Resource<List<SelectSeasonWithEpisodes>>> =
        networkBoundResource(
            query = { seasonCache.observeShowEpisodes(showId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { traktService.getSeasonWithEpisodes(showId) },
            saveFetchResult = { mapAndCache(showId, it) },
            onFetchFailed = {
                Logger.withTag("observeSeasonEpisodes").e(it) { it.message ?: "Error" }
            },
            coroutineDispatcher = dispatcher
        )

    override fun observeSeasonEpisodes(showId: Int): Flow<List<SelectSeasonWithEpisodes>> =
        seasonCache.observeShowEpisodes(showId)


    private fun mapAndCache(showId: Int, response: List<TraktSeasonEpisodesResponse>) {
        response.forEach { season ->
            episodesCache.insert(season.toEpisodeCacheList())

            seasonCache.insert(
                Season_with_episodes(
                    show_id = showId,
                    season_id = season.ids.trakt,
                    season_number = season.number,
                )
            )
        }
    }
}
