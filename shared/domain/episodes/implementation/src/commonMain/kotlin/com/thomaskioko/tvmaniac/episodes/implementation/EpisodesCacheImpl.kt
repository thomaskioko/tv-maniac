package com.thomaskioko.tvmaniac.episodes.implementation

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.EpisodeArtByShowId
import com.thomaskioko.tvmaniac.core.db.EpisodesByShowId
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import kotlinx.coroutines.flow.Flow
import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache

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

    override fun observeEpisodesByShowId(id: Int): Flow<List<EpisodesByShowId>> =
        episodeQueries.episodesByShowId(id)
            .asFlow()
            .mapToList()

    override fun observeEpisodeArtByShowId(id: Int): Flow<List<EpisodeArtByShowId>> =
        episodeQueries.episodeArtByShowId(id)
            .asFlow()
            .mapToList()
}
