package com.thomaskioko.tvmaniac.datasource.repository.tvshow

import com.thomaskioko.tvmaniac.datasource.cache.model.TvShow
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory

interface TvShowsRepository  {

    suspend fun getTvShow(tvShowId: Int) : TvShow

    suspend fun getPopularTvShows(page: Int) : List<TvShow>

    suspend fun getTopRatedTvShows(page: Int) : List<TvShow>

    suspend fun getTrendingShows(timeWindow: String): List<TvShow>

    suspend fun getFeaturedShows(): List<TvShow>

    suspend fun getShowsByCategoryAndWindow(category: TvShowCategory, timeWindow: TimeWindow): List<TvShow>
}