package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import me.tatarka.inject.annotations.Inject
import com.thomaskioko.tvmaniac.core.db.Episodes as EpisodeCache

@Inject
class EpisodesDaoImpl(
    private val database: TvManiacDatabase,
) : EpisodesDao {

    private val episodeQueries get() = database.episodesQueries

    override fun insert(entity: EpisodeCache) {
        database.transaction {
            episodeQueries.insertOrReplace(
                trakt_id = entity.trakt_id,
                season_id = entity.season_id,
                tmdb_id = entity.tmdb_id,
                title = entity.title,
                overview = entity.overview,
                ratings = entity.ratings,
                runtime = entity.runtime,
                votes = entity.votes,
                episode_number = entity.episode_number,
            )
        }
    }

    override fun insert(list: List<EpisodeCache>) {
        list.map { insert(it) }
    }

    override fun delete(id: Long) {
        episodeQueries.delete(id)
    }

    override fun deleteAll() {
        database.transaction {
            episodeQueries.deleteAll()
        }
    }
}
