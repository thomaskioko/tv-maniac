package com.thomaskioko.tvmaniac.datasource.repository.tvshow

import com.thomaskioko.tvmaniac.datasource.cache.model.TvShowsEntity
import com.thomaskioko.tvmaniac.datasource.enums.TimeWindow
import com.thomaskioko.tvmaniac.datasource.enums.TvShowCategory

interface TvShowsRepository  {

    suspend fun getTvShow(tvShowId: Int) : TvShowsEntity

    suspend fun getPopularTvShows(page: Int) : List<TvShowsEntity>

    suspend fun getTopRatedTvShows(page: Int) : List<TvShowsEntity>

    suspend fun getTrendingShows(timeWindow: String): List<TvShowsEntity>

    suspend fun getShowsByCategoryAndWindow(category: TvShowCategory, timeWindow: TimeWindow): List<TvShowsEntity>
}