package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.EpisodeArt
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import com.thomaskioko.tvmaniac.core.db.Episodes as EpisodeCache

@Inject
class EpisodesDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
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

    override fun observeEpisodeArtByShowId(): Flow<List<EpisodeArt>> =
        episodeQueries.episodeArt()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun delete(id: Long) {
        episodeQueries.delete(id)
    }

    override fun deleteAll() {
        database.transaction {
            episodeQueries.deleteAll()
        }
    }
}
