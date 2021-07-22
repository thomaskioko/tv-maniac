package com.thomaskioko.tvmaniac.datasource.cache.shows

import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.model.TvShows
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowsEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowsEntityList

class TvShowCacheImpl(
    private val database: TvManiacDatabase
) : TvShowCache {

    override fun insert(entity: TvShows) {
        database.tvShowQueries.transaction {
            database.tvShowQueries.insertOrReplace(
                id = entity.id.toLong(),
                title = entity.title,
                description = entity.overview,
                language = entity.language,
                poster_image_url = entity.posterImageUrl,
                backdrop_image_url = entity.backdropImageUrl,
                votes = entity.votes.toLong(),
                vote_average = entity.averageVotes,
                genre_ids = entity.genreIds,
                show_category = entity.showCategory,
                time_window = entity.timeWindow
            )
        }
    }

    override fun insert(list: List<TvShows>) {
        list.forEach { insert(it) }
    }

    override fun getTvShow(showId: Int): TvShows {
        return database.tvShowQueries.selectByShowId(
            id = showId.toLong()
        ).executeAsOne().toTvShowsEntity()
    }


    override fun getTvShows(): List<TvShows> {
        return database.tvShowQueries.selectAll()
            .executeAsList()
            .toTvShowsEntityList()
    }

    override fun getTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<TvShows> {
        return database.tvShowQueries.selectByShowIdAndWindow(
            show_category = category,
            time_window = timeWindow
        ).executeAsList()
            .toTvShowsEntityList()
    }

    override fun getFeaturedTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<TvShows> {
        return database.tvShowQueries.selectFeatured(
            show_category = category,
            time_window = timeWindow
        )
            .executeAsList()
            .toTvShowsEntityList()
    }

    override fun updateTvShowDetails(entity: TvShows) {
        database.tvShowQueries.updateTvShow(
            id = entity.id.toLong(),
            seasons = entity.seasonsList
        )
    }

    override fun deleteTvShows() {
        database.tvShowQueries.deleteAll()
    }
}