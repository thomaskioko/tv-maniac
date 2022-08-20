package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.tmdb.api.model.GenresResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TvShowsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TmdbServiceImpl(
    private val httpClient: HttpClient,
) : TmdbService {

    override suspend fun getTopRatedShows(page: Int): TvShowsResponse =
        httpClient.get("3/tv/top_rated") {
            parameter("page", page)
            parameter("sort_by", "popularity.desc")
        }.body()


    override suspend fun getPopularShows(page: Int): TvShowsResponse =
        httpClient.get("3/tv/popular") {
            parameter("page", page)
            parameter("sort_by", "popularity.desc")
        }.body()


    override suspend fun getSimilarShows(showId: Long): TvShowsResponse =
        httpClient.get("3/tv/$showId/recommendations")
            .body()

    override suspend fun getRecommendations(showId: Long): TvShowsResponse =
        httpClient.get("3/tv/$showId/recommendations")
            .body()


    override suspend fun getTvShowDetails(showId: Long): ShowDetailResponse =
        httpClient.get("3/tv/$showId")
            .body()


    override suspend fun getSeasonDetails(tvShowId: Long, seasonNumber: Long): SeasonResponse =
        httpClient.get("3/tv/$tvShowId/season/$seasonNumber")
            .body()

    override suspend fun getTrendingShows(page: Int): TvShowsResponse = httpClient
        .get("3/trending/tv/week") {
            parameter("page", page)
        }.body()

    override suspend fun getAllGenres(): GenresResponse = httpClient
        .get("3/genre/tv/list")
        .body()

    override suspend fun getTrailers(showId: Long): TrailersResponse = httpClient
        .get("3/tv/$showId/videos")
        .body()
}
