package com.thomaskioko.tvmaniac.datasource.network

import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse
import com.thomaskioko.tvmaniac.shared.BuildKonfig
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class TvShowsServiceImpl(
    private val httpClient: HttpClient,
) : TvShowsService {

    override suspend fun getTopRatedShows(page: Int): TvShowsResponse {
        return httpClient.get("${BuildKonfig.TMDB_API_URL}tv/top_rated") {
            accept(ContentType.Application.Json)
            parameter("api_key", BuildKonfig.TMDB_API_KEY)
            parameter("page", page)
        }
    }

    override suspend fun getPopularShows(page: Int): TvShowsResponse {
        return httpClient.get("${BuildKonfig.TMDB_API_URL}tv/popular") {
            accept(ContentType.Application.Json)
            parameter("api_key", BuildKonfig.TMDB_API_KEY)
            parameter("page", page)
        }
    }
}