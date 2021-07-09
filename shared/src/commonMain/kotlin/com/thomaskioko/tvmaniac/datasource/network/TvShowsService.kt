package com.thomaskioko.tvmaniac.datasource.network

import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse

interface TvShowsService {

    suspend fun getTopRatedShows(page: Int) : TvShowsResponse

    suspend fun getPopularShows(page: Int) : TvShowsResponse
}