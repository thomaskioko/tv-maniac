package com.thomaskioko.tvmaniac.datasource.network.api

import com.thomaskioko.tvmaniac.datasource.network.model.EpisodeDetailResponse
import com.thomaskioko.tvmaniac.datasource.network.model.SeasonResponse
import com.thomaskioko.tvmaniac.datasource.network.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse
import com.thomaskioko.tvmaniac.shared.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TvShowsServiceImpl(
    private val httpClient: HttpClient,
) : TvShowsService {

    override suspend fun getTopRatedShows(page: Int): TvShowsResponse {
        return httpClient.get("${BuildKonfig.TMDB_API_URL}tv/top_rated")
        {
            parameter("page", page)
        }
    }

    override suspend fun getPopularShows(page: Int): TvShowsResponse {
        return httpClient.get("${BuildKonfig.TMDB_API_URL}tv/popular")
        {
            parameter("page", page)
        }
    }

    override suspend fun getTvShowDetails(showId: Int): ShowDetailResponse {
        return httpClient.get("${BuildKonfig.TMDB_API_URL}tv/$showId")
    }

    override suspend fun getSeasonDetails(tvShowId: Int, seasonNumber: Int): SeasonResponse {
        return httpClient.get("${BuildKonfig.TMDB_API_URL}tv/$tvShowId/season/$seasonNumber")
    }

    override suspend fun getTvShowSeasonEpisode(
        showId: Int,
        seasonNumber: Int,
        episodeNumber: Int
    ): EpisodeDetailResponse {
        return httpClient.get("${BuildKonfig.TMDB_API_URL}tv/$showId/season/$seasonNumber/episode/$episodeNumber")
    }
}