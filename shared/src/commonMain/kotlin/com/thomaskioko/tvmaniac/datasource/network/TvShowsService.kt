package com.thomaskioko.tvmaniac.datasource.network

import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse

interface TvShowsService {

    suspend fun getTopRatedShows() : TvShowsResponse

    suspend fun getPopularShows() : TvShowsResponse
}