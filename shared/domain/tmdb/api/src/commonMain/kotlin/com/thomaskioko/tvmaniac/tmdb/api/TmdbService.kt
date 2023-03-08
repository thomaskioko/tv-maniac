package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.tmdb.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.GenresResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.SeasonResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ShowDetailResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TrailersResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbResponse

interface TmdbService {

    suspend fun getTopRatedShows(page: Int): TmdbResponse

    suspend fun getPopularShows(page: Int): TmdbResponse

    suspend fun getSimilarShows(showId: Int): TmdbResponse

    suspend fun getRecommendations(showId: Int): TmdbResponse

    suspend fun getTvShowDetails(showId: Int): ShowDetailResponse

    suspend fun getSeasonDetails(tvShowId: Int, seasonNumber: Int): SeasonResponse

    suspend fun getEpisodeDetails(
        tmdbShow: Int,
        ssnNumber: Int,
        epNumber: Int
    ): EpisodesResponse

    suspend fun getTrendingShows(page: Int): TmdbResponse

    suspend fun getAllGenres(): GenresResponse

    suspend fun getTrailers(showId: Int): TrailersResponse
}
