package com.thomaskioko.tvmaniac.datasource.repository.tvshow

import com.thomaskioko.tvmaniac.datasource.cache.model.TvShows
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory

interface TvShowsRepository  {

    suspend fun getTvShow(tvShowId: Int) : TvShows

    suspend fun getPopularTvShows(page: Int) : List<TvShows>

    suspend fun getTopRatedTvShows(page: Int) : List<TvShows>

    suspend fun getTrendingShows(timeWindow: String): List<TvShows>

    suspend fun getFeaturedShows(): List<TvShows>

    suspend fun getShowsByCategoryAndWindow(category: TvShowCategory, timeWindow: TimeWindow): List<TvShows>
}