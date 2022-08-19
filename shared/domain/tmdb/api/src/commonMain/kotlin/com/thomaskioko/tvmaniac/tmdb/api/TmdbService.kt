package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.remote.api.model.GenresResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TvShowsResponse

interface TmdbService {

    suspend fun getTopRatedShows(page: Int): TvShowsResponse

    suspend fun getPopularShows(page: Int): TvShowsResponse

    suspend fun getSimilarShows(showId: Long): TvShowsResponse

    suspend fun getRecommendations(showId: Long): TvShowsResponse

    suspend fun getTvShowDetails(showId: Long): ShowDetailResponse

    suspend fun getSeasonDetails(tvShowId: Long, seasonNumber: Long): SeasonResponse

    suspend fun getTrendingShows(page: Int): TvShowsResponse

    suspend fun getAllGenres(): GenresResponse

    suspend fun getTrailers(showId: Long): TrailersResponse
}
