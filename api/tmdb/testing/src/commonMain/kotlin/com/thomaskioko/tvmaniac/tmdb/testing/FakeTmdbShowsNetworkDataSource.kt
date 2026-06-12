package com.thomaskioko.tvmaniac.tmdb.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbGenreResult
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult

public class FakeTmdbShowsNetworkDataSource(
    private var findShowByExternalIdResponse: ApiResponse<Long?> = ApiResponse.Success(null),
) : TmdbShowsNetworkDataSource {

    public fun setFindShowByExternalId(response: ApiResponse<Long?>) {
        findShowByExternalIdResponse = response
    }

    override suspend fun getAiringToday(page: Long): ApiResponse<TmdbShowResult> =
        error("FakeTmdbShowsNetworkDataSource: getAiringToday not configured")

    override suspend fun discoverShows(
        page: Long,
        sortBy: String,
        genres: String?,
        watchProviders: String?,
        screenedTheatrically: Boolean,
        voteAverageGte: Double?,
        voteCountGte: Int?,
        firstAirDateGte: String?,
        firstAirDateLte: String?,
    ): ApiResponse<TmdbShowResult> =
        error("FakeTmdbShowsNetworkDataSource: discoverShows not configured")

    override suspend fun getPopularShows(page: Long): ApiResponse<TmdbShowResult> =
        error("FakeTmdbShowsNetworkDataSource: getPopularShows not configured")

    override suspend fun getTopRatedShows(page: Long): ApiResponse<TmdbShowResult> =
        error("FakeTmdbShowsNetworkDataSource: getTopRatedShows not configured")

    override suspend fun getTrendingShows(timeWindow: String): ApiResponse<TmdbShowResult> =
        error("FakeTmdbShowsNetworkDataSource: getTrendingShows not configured")

    override suspend fun getUpComingShows(
        year: Int,
        page: Long,
        sortBy: String,
    ): ApiResponse<TmdbShowResult> =
        error("FakeTmdbShowsNetworkDataSource: getUpComingShows not configured")

    override suspend fun getUpComingShows(
        page: Long,
        firstAirDate: String,
        lastAirDate: String,
        sortBy: String,
    ): ApiResponse<TmdbShowResult> =
        error("FakeTmdbShowsNetworkDataSource: getUpComingShows not configured")

    override suspend fun searchShows(query: String): ApiResponse<TmdbShowResult> =
        error("FakeTmdbShowsNetworkDataSource: searchShows not configured")

    override suspend fun getShowGenres(): ApiResponse<TmdbGenreResult> =
        error("FakeTmdbShowsNetworkDataSource: getShowGenres not configured")

    override suspend fun getShowCredits(tmdbId: Long): ApiResponse<CreditsResponse> =
        error("FakeTmdbShowsNetworkDataSource: getShowCredits not configured")

    override suspend fun findShowByExternalId(
        externalId: String,
        source: String,
    ): ApiResponse<Long?> = findShowByExternalIdResponse
}
