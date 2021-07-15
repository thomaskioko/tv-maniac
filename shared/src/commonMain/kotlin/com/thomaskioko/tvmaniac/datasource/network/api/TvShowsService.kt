package com.thomaskioko.tvmaniac.datasource.network.api

import com.thomaskioko.tvmaniac.datasource.network.model.EpisodeDetailResponse
import com.thomaskioko.tvmaniac.datasource.network.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.datasource.network.model.ShowSeasonsResponse
import com.thomaskioko.tvmaniac.datasource.network.model.TvShowsResponse

interface TvShowsService {

    suspend fun getTopRatedShows(page: Int) : TvShowsResponse

    suspend fun getPopularShows(page: Int) : TvShowsResponse

    suspend fun getTvSeasonDetails(showId: Int) : ShowDetailResponse

    suspend fun getTvShowSeasons(showId: Int, seasonNumber : Int) : ShowSeasonsResponse

    suspend fun getTvShowSeasonEpisode(showId: Int, seasonNumber : Int, episodeNumber: Int) : EpisodeDetailResponse

}