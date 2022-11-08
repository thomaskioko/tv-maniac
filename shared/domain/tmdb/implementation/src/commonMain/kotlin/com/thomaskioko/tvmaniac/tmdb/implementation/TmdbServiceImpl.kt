package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.tmdb.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TmdbServiceImpl(
    private val httpClient: HttpClient,
) : TmdbService {

    override suspend fun getTvShowDetails(showId: Int): ShowDetailResponse =
        httpClient.get("3/tv/$showId")
            .body()

    override suspend fun getEpisodeDetails(
        tmdbShow: Int,
        ssnNumber: Int,
        epNumber: Int
    ): EpisodesResponse =
        httpClient.get("3/tv/$tmdbShow/season/$ssnNumber/episode/$epNumber")
            .body()


    override suspend fun getTrailers(showId: Int): TrailersResponse = httpClient
        .get("3/tv/$showId/videos")
        .body()
}
