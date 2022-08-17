package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.remote.api.model.GenresResponse
import com.thomaskioko.tvmaniac.remote.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.remote.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.remote.api.model.TrailersResponse
import com.thomaskioko.tvmaniac.remote.api.model.TvShowsResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TmdbServiceImpl(
    private val httpClient: HttpClient,
) : TmdbService {

    override suspend fun getTopRatedShows(page: Int): TvShowsResponse {
        return httpClient.get("3/tv/top_rated") {
            parameter("api_key", BuildKonfig.TMDB_API_KEY)
            parameter("page", page)
            parameter("sort_by", "popularity.desc")
        }
    }

    override suspend fun getPopularShows(page: Int): TvShowsResponse {
        return httpClient.get("3/tv/popular") {
            parameter("api_key", BuildKonfig.TMDB_API_KEY)
            parameter("page", page)
            parameter("sort_by", "popularity.desc")
        }
    }

    override suspend fun getSimilarShows(showId: Long): TvShowsResponse {
        return httpClient.get("3/tv/$showId/recommendations"){
            parameter("api_key", BuildKonfig.TMDB_API_KEY)
        }
    }

    override suspend fun getRecommendations(showId: Long): TvShowsResponse {
        return httpClient.get("3/tv/$showId/recommendations"){
            parameter("api_key", BuildKonfig.TMDB_API_KEY)
        }
    }

    override suspend fun getTvShowDetails(showId: Long): ShowDetailResponse {
        return httpClient.get("3/tv/$showId"){
            parameter("api_key", BuildKonfig.TMDB_API_KEY)
        }
    }

    override suspend fun getSeasonDetails(tvShowId: Long, seasonNumber: Long): SeasonResponse {
        return httpClient.get("3/tv/$tvShowId/season/$seasonNumber"){
            parameter("api_key", BuildKonfig.TMDB_API_KEY)
        }
    }

    override suspend fun getTrendingShows(page: Int): TvShowsResponse {
        return httpClient.get("3/trending/tv/week") {
            parameter("api_key", BuildKonfig.TMDB_API_KEY)
            parameter("page", page)
        }
    }

    override suspend fun getAllGenres(): GenresResponse {
        return httpClient.get("3/genre/tv/list"){
            parameter("api_key", BuildKonfig.TMDB_API_KEY)
        }
    }

    override suspend fun getTrailers(showId: Long): TrailersResponse {
        return httpClient.get("3/tv/$showId/videos"){
            parameter("api_key", BuildKonfig.TMDB_API_KEY)
        }
    }
}
