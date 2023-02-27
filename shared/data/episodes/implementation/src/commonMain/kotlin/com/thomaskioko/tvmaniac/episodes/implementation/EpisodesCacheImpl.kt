package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache
import com.thomaskioko.tvmaniac.core.db.EpisodeArtByShowId
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache

class EpisodesCacheImpl(
    private val database: TvManiacDatabase
) : EpisodesCache {

    private val episodeQueries get() = database.episodeQueries

    override fun insert(entity: EpisodeCache) {
        database.transaction {
            episodeQueries.insertOrReplace(
                id = entity.id,
                season_id = entity.season_id,
                tmdb_id = entity.tmdb_id,
                title = entity.title,
                overview = entity.overview,
                ratings = entity.ratings,
                runtime = entity.runtime,
                votes = entity.votes,
                episode_number = entity.episode_number
            )
        }
    }

    override fun insert(list: List<EpisodeCache>) {
        list.map { insert(it) }
    }

    override fun observeEpisodeArtByShowId(id: Long): List<EpisodeArtByShowId> =
        episodeQueries.episodeArtByShowId()
            .executeAsList()
}
