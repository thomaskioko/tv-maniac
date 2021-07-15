package com.thomaskioko.tvmaniac.datasource.network.api

import com.thomaskioko.tvmaniac.datasource.network.model.EpisodeDetailResponse
import com.thomaskioko.tvmaniac.datasource.network.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.datasource.network.model.ShowSeasonsResponse
import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TvShowsServiceImpl(
    private val httpClient: HttpClient,
) : TvShowsService {

    override suspend fun getTopRatedShows(page: Int): TvShowsResponse {
        return httpClient.get("tv/top_rated")
        {
            parameter("page", page)
        }
    }

    override suspend fun getPopularShows(page: Int): TvShowsResponse {
        return httpClient.get("tv/popular")
        {
            parameter("page", page)
        }
    }

    override suspend fun getTvSeasonDetails(showId: Int): ShowDetailResponse {
        return httpClient.get("tv/$showId")
    }

    override suspend fun getTvShowSeasons(showId: Int, seasonNumber: Int): ShowSeasonsResponse {
        return httpClient.get("tv/$showId/season/$seasonNumber")
    }

    override suspend fun getTvShowSeasonEpisode(
        showId: Int,
        seasonNumber: Int,
        episodeNumber: Int
    ): EpisodeDetailResponse {
        return httpClient.get("tv/$showId/season/$seasonNumber/episode/$episodeNumber")
    }
}