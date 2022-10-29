package com.thomaskioko.tvmaniac.seasonepisodes.implementation

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.core.db.Season_with_episodes
import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.core.util.network.networkBoundResource
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonEpisodesRepository
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesCache
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonEpisodesResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class SeasonEpisodesRepositoryImpl(
    private val traktService: TraktService,
    private val episodesCache: EpisodesCache,
    private val seasonWithEpisodesCache: SeasonWithEpisodesCache,
    private val dispatcher: CoroutineDispatcher,
) : SeasonEpisodesRepository {

    override fun updateSeasonEpisodes(showId: Int): Flow<Resource<List<SelectSeasonWithEpisodes>>> =
        networkBoundResource(
            query = { seasonWithEpisodesCache.observeShowEpisodes(showId) },
            shouldFetch = { it.isNullOrEmpty() },
            fetch = { traktService.getSeasonWithEpisodes(showId) },
            saveFetchResult = { mapAndCache(showId, it) },
            onFetchFailed = {
                Logger.withTag("observeSeasonEpisodes").e(it) { it.message ?: "Error" }
            },
            coroutineDispatcher = dispatcher
        )

    override fun observeSeasonEpisodes(showId: Int): Flow<List<SelectSeasonWithEpisodes>> =
        seasonWithEpisodesCache.observeShowEpisodes(showId)


    private fun mapAndCache(showId: Int, response: List<TraktSeasonEpisodesResponse>) {
        response.forEach { season ->
            episodesCache.insert(season.toEpisodeCacheList())

            seasonWithEpisodesCache.insert(
                Season_with_episodes(
                    show_id = showId,
                    season_id = season.ids.trakt,
                    season_number = season.number,
                )
            )
        }
    }
}
