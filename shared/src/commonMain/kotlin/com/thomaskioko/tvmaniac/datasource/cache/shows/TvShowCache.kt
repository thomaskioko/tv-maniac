package com.thomaskioko.tvmaniac.datasource.cache.shows

import com.thomaskioko.tvmaniac.datasource.cache.model.TvShows
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory

interface TvShowCache {

    fun insert(entity: TvShows)

    fun insert(list: List<TvShows>)

    fun getTvShow(showId: Int): TvShows

    fun getTvShows(): List<TvShows>

    fun getTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<TvShows>

    fun getFeaturedTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<TvShows>

    fun updateTvShowDetails(entity: TvShows)

    fun deleteTvShows()
}