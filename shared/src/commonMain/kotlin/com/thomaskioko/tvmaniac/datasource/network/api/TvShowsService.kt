package com.thomaskioko.tvmaniac.datasource.network.api

import com.thomaskioko.tvmaniac.datasource.network.model.GenresResponse
import com.thomaskioko.tvmaniac.datasource.network.model.SeasonResponse
import com.thomaskioko.tvmaniac.datasource.network.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.datasource.network.model.TrailersResponse
import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse

interface TvShowsService {

    suspend fun getTopRatedShows(page: Int) : TvShowsResponse

    suspend fun getPopularShows(page: Int) : TvShowsResponse

    suspend fun getTvShowDetails(showId: Int) : ShowDetailResponse

    suspend fun getSeasonDetails(tvShowId: Int, seasonNumber : Int) : SeasonResponse

    suspend fun getTrendingShows(timeWindow: String) : TvShowsResponse

    suspend fun getAllGenres() : GenresResponse

    suspend fun getTrailers(showId: Int) : TrailersResponse

}