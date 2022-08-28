package com.thomaskioko.tvmaniac.details.implementation.cache

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.db.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import kotlinx.coroutines.flow.Flow

class TvShowCacheImpl(
    private val database: TvManiacDatabase
) : TvShowCache {

    override fun insert(show: Show) {
        database.showQueries.transaction {
            database.showQueries.insertOrReplace(
                id = show.id,
                title = show.title,
                description = show.description,
                language = show.language,
                poster_image_url = show.poster_image_url,
                backdrop_image_url = show.backdrop_image_url,
                votes = show.votes,
                vote_average = show.vote_average,
                genre_ids = show.genre_ids,
                year = show.year,
                status = show.status,
                popularity = show.popularity
            )
        }
    }

    override fun insert(list: List<Show>) {
        list.forEach { insert(it) }
    }

    override fun updateShow(show: Show) {
        database.transaction {
            database.showQueries.updateTvShow(
                id = show.id,
                status = show.status,
                number_of_seasons = show.number_of_seasons,
                number_of_episodes = show.number_of_episodes
            )
        }
    }

    override fun observeTvShow(showId: Long): Flow<Show?> {
        return database.showQueries.selectByShowId(showId)
            .asFlow()
            .mapToOneOrNull()
    }

    override fun observeTvShows(): Flow<List<Show>> {
        return database.showQueries.selectAll()
            .asFlow()
            .mapToList()
    }

    override fun getShowAirEpisodes(showId: Long): Flow<List<AirEpisodesByShowId>> {
        return database.lastAirEpisodeQueries.airEpisodesByShowId(
            show_id = showId
        ).asFlow()
            .mapToList()
    }

    override fun deleteTvShows() {
        database.showQueries.deleteAll()
    }
}
