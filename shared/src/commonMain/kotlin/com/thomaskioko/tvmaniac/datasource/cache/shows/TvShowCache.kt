package com.thomaskioko.tvmaniac.datasource.cache.shows

import com.thomaskioko.tvmaniac.datasource.cache.model.TvShow
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory

interface TvShowCache {

    fun insert(entity: TvShow)

    fun insert(list: List<TvShow>)

    fun getTvShow(showId: Int): TvShow

    fun getTvShows(): List<TvShow>

    fun getTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<TvShow>

    fun getFeaturedTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<TvShow>

    fun updateTvShowDetails(entity: TvShow)

    fun deleteTvShows()
}