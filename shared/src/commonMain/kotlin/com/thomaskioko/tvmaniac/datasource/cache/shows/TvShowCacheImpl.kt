package com.thomaskioko.tvmaniac.datasource.cache.shows

import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.Tv_show
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowsEntity
import com.thomaskioko.tvmaniac.datasource.mapper.toTvShowsEntityList
import com.thomaskioko.tvmaniac.presentation.model.TvShow

class TvShowCacheImpl(
    private val database: TvManiacDatabase
) : TvShowCache {

    override fun insert(entity: TvShow) {
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
                time_window = entity.timeWindow,
                year = entity.year
            )
        }
    }

    override fun insert(list: List<TvShow>) {
        list.forEach { insert(it) }
    }

    override fun getTvShow(showId: Int): TvShow {
        return database.tvShowQueries.selectByShowId(
            id = showId.toLong()
        ).executeAsOne().toTvShowsEntity()
    }


    override fun getTvShows(): List<TvShow> {
        return database.tvShowQueries.selectAll()
            .executeAsList()
            .toTvShowsEntityList()
    }

    override fun getTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<Tv_show> {
        return database.tvShowQueries.selectByShowIdAndWindow(
            show_category = category,
            time_window = timeWindow
        ).executeAsList()
    }

    override fun getFeaturedTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<Tv_show> {
        return database.tvShowQueries.selectFeatured(
            show_category = category,
            time_window = timeWindow
        )
            .executeAsList()
    }

    override fun updateTvShowDetails(entity: TvShow) {
        database.tvShowQueries.updateTvShow(
            id = entity.id.toLong(),
            seasons = entity.seasonsList
        )
    }

    override fun deleteTvShows() {
        database.tvShowQueries.deleteAll()
    }
}