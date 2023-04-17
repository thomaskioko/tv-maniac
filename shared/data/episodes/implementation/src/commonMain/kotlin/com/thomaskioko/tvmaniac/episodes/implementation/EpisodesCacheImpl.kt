package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.db.Episode as EpisodeCache
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.db.EpisodeArt
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class EpisodesCacheImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : EpisodesCache {

    private val episodeQueries get() = database.episodeQueries

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
                episode_number = entity.episode_number
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
}
