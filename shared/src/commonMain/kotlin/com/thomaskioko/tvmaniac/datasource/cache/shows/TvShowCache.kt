package com.thomaskioko.tvmaniac.datasource.cache.shows

import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory

interface TvShowCache {

    fun insert(entity: TvShowsEntity)

    fun insert(entityList: List<TvShowsEntity>)

    fun getTvShow(showId: Int): TvShowsEntity

    fun getTvShows(): List<TvShowsEntity>

    fun getTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<TvShowsEntity>

    fun updateTvShowDetails(entity: TvShowsEntity)

    fun deleteTvShows()
}