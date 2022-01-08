package com.thomaskioko.tvmaniac.remote.api

import com.thomaskioko.tvmaniac.remote.api.model.GenresResponse
import com.thomaskioko.tvmaniac.remote.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.remote.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.remote.api.model.TrailersResponse
import com.thomaskioko.tvmaniac.remote.api.model.TvShowsResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TvShowsServiceImpl(
    private val httpClient: HttpClient,
) : TvShowsService {

    override suspend fun getTopRatedShows(page: Int): TvShowsResponse {
        return httpClient.get("3/tv/top_rated") {
            parameter("page", page)
            parameter("sort_by", "popularity.desc")
        }
    }

    override suspend fun getPopularShows(page: Int): TvShowsResponse {
        return httpClient.get("3/tv/popular") {
            parameter("page", page)
            parameter("sort_by", "popularity.desc")
        }
    }

    override suspend fun getTvShowDetails(showId: Int): ShowDetailResponse {
        return httpClient.get("3/tv/$showId")
    }

    override suspend fun getSeasonDetails(tvShowId: Int, seasonNumber: Int): SeasonResponse {
        return httpClient.get("3/tv/$tvShowId/season/$seasonNumber")
    }

    override suspend fun getTrendingShows(page: Int): TvShowsResponse {
        return httpClient.get("3/trending/tv/week") {
            parameter("page", page)
        }
    }

    override suspend fun getAllGenres(): GenresResponse {
        return httpClient.get("3/genre/tv/list")
    }

    override suspend fun getTrailers(showId: Int): TrailersResponse {
        return httpClient.get("3/tv/$showId/videos")
    }
}
