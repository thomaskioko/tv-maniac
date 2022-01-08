package com.thomaskioko.tvmaniac.remote.api

import com.thomaskioko.tvmaniac.remote.api.model.GenresResponse
import com.thomaskioko.tvmaniac.remote.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.remote.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.remote.api.model.TrailersResponse
import com.thomaskioko.tvmaniac.remote.api.model.TvShowsResponse

interface TvShowsService {

    suspend fun getTopRatedShows(page: Int): TvShowsResponse

    suspend fun getPopularShows(page: Int): TvShowsResponse

    suspend fun getTvShowDetails(showId: Int): ShowDetailResponse

    suspend fun getSeasonDetails(tvShowId: Int, seasonNumber: Int): SeasonResponse

    suspend fun getTrendingShows(page: Int): TvShowsResponse

    suspend fun getAllGenres(): GenresResponse

    suspend fun getTrailers(showId: Int): TrailersResponse
}
