package com.thomaskioko.tvmaniac.episodes.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import kotlinx.coroutines.flow.Flow
import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache

class EpisodesCacheImpl(
    private val database: TvManiacDatabase
) : EpisodesCache {

    private val episodeQueries get() = database.episodeQueries

    override fun insert(entity: EpisodeCache) {
        episodeQueries.insertOrReplace(
            id = entity.id,
            season_id = entity.season_id,
            tmdb_id = entity.tmdb_id,
            title = entity.title,
            overview = entity.overview,
            image_url = entity.image_url,
            vote_average = entity.vote_average,
            votes = entity.votes,
            episode_number = entity.episode_number
        )
    }

    override fun insert(list: List<EpisodeCache>) {
        list.map { insert(it) }
    }

    override fun updatePoster(episodeId: Int, posterPath: String?) {
        episodeQueries.updateEpisode(
            tmdb_id = episodeId,
            image_url = posterPath
        )
    }

    override fun observeEpisode(seasonId: Int): Flow<List<EpisodesBySeasonId>> =
        episodeQueries.episodesBySeasonId(seasonId)
            .asFlow()
            .mapToList()

    override fun getEpisode(seasonId: Int): List<EpisodesBySeasonId> =
        episodeQueries.episodesBySeasonId(seasonId)
            .executeAsList()
}
