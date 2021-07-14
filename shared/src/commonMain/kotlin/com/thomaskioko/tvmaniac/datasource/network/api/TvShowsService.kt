package com.thomaskioko.tvmaniac.datasource.network.api

import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse

interface TvShowsService {

    suspend fun getTopRatedShows(page: Int) : TvShowsResponse

    suspend fun getPopularShows(page: Int) : TvShowsResponse
}