package com.thomaskioko.tvmaniac.datasource.cache.shows

import com.thomaskioko.tvmaniac.datasource.cache.Tv_show
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory
import com.thomaskioko.tvmaniac.presentation.model.TvShow

interface TvShowCache {

    fun insert(entity: TvShow)

    fun insert(list: List<TvShow>)

    fun getTvShow(showId: Int): TvShow

    fun getTvShows(): List<TvShow>

    fun getTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<Tv_show>

    fun getFeaturedTvShows(category: TvShowCategory, timeWindow: TimeWindow): List<Tv_show>

    fun updateTvShowDetails(entity: TvShow)

    fun deleteTvShows()
}