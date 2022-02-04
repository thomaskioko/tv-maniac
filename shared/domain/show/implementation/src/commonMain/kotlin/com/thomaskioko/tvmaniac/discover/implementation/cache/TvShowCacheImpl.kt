package com.thomaskioko.tvmaniac.discover.implementation.cache

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.thomaskioko.tvmaniac.datasource.cache.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.showcommon.api.TvShowCache
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
                popularity = show.popularity,
                following = show.following
            )
        }
    }

    override fun insert(list: List<Show>) {
        list.forEach { insert(it) }
    }

    override fun observeTvShow(showId: Long): Flow<Show> {
        return database.showQueries.selectByShowId(
            id = showId
        )
            .asFlow()
            .mapToOne()
    }

    override fun observeTvShows(): Flow<List<Show>> {
        return database.showQueries.selectAll()
            .asFlow()
            .mapToList()
    }

    override fun observeFollowing(): Flow<List<Show>> {
        return database.showQueries.selectFollowinglist()
            .asFlow()
            .mapToList()
    }

    override fun getShowAirEpisodes(showId: Long): Flow<List<AirEpisodesByShowId>> {
        return database.lastAirEpisodeQueries.airEpisodesByShowId(
            show_id = showId
        ).asFlow()
            .mapToList()
    }

    override fun updateFollowingShow(showId: Long, following: Boolean) {
        database.showQueries.updateFollowinglist(
            following = following,
            id = showId
        )
    }

    override fun deleteTvShows() {
        database.showQueries.deleteAll()
    }
}
