package com.thomaskioko.tvmaniac.datasource.cache.shows

import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory

class TvShowCacheImpl(
    private val database: TvManiacDatabase
) : TvShowCache {

    override fun insert(show: Show) {
        database.tvShowQueries.transaction {
            database.tvShowQueries.insertOrReplace(
                id = show.id,
                title = show.title,
                description = show.description,
                language = show.language,
                poster_image_url = show.poster_image_url,
                backdrop_image_url = show.backdrop_image_url,
                votes = show.votes,
                vote_average = show.vote_average,
                genre_ids = show.genre_ids,
                show_category = show.show_category,
                time_window = show.time_window,
                year = show.year,
                status = show.status,
                popularity = show.popularity
            )
        }
    }

    override fun insert(list: List<Show>) {
        list.forEach { insert(it) }
    }

    override fun getTvShow(showId: Int): Show {
        return database.tvShowQueries.selectByShowId(
            id = showId.toLong()
        ).executeAsOne()
    }


    override fun getTvShows(): List<Show> {
        return database.tvShowQueries.selectAll()
            .executeAsList()
    }

    override fun getTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<Show> {
        return database.tvShowQueries.selectByShowIdAndWindow(
            show_category = category,
            time_window = timeWindow
        ).executeAsList()
    }

    override fun getFeaturedTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<Show> {
        return database.tvShowQueries.selectFeatured(
            show_category = category,
            time_window = timeWindow
        ).executeAsList()
    }

    override fun updateShowDetails(showId: Int, showStatus: String, seasonIds: List<Int>) {
        database.tvShowQueries.updateTvShow(
            id = showId.toLong(),
            season_ids = seasonIds,
            status = showStatus
        )
    }

    override fun deleteTvShows() {
        database.tvShowQueries.deleteAll()
    }
}