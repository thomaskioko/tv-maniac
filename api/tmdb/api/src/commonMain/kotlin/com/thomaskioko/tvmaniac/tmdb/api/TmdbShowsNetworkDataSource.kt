package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbGenreResult
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult

public const val DEFAULT_API_PAGE: Long = 1
public const val DEFAULT_SORT_ORDER: String = "popularity.desc"

public interface TmdbShowsNetworkDataSource {

    /**
     * Get a list of TV shows airing today.
     *
     * @param page Page number
     */
    public suspend fun getAiringToday(page: Long): ApiResponse<TmdbShowResult>

    /**
     * Find TV shows using over 30 filters and sort options. Available filter options asc & desc
     * popularity.asc, primary_release_date.desc, vote_average.desc, vote_count.desc
     *
     * @param page Page number
     * @param sortBy Default: popularity.desc.
     * @param genres Comma separated list of genre ids.
     * @param voteAverageGte Minimum vote average (e.g., 7.0)
     * @param voteCountGte Minimum vote count (e.g., 100)
     * @param firstAirDateGte Shows aired after this date (YYYY-MM-DD)
     * @param firstAirDateLte Shows aired before this date (YYYY-MM-DD)
     */
    public suspend fun discoverShows(
        page: Long = DEFAULT_API_PAGE,
        sortBy: String = DEFAULT_SORT_ORDER,
        genres: String? = null,
        watchProviders: String? = null,
        screenedTheatrically: Boolean = true,
        voteAverageGte: Double? = null,
        voteCountGte: Int? = null,
        firstAirDateGte: String? = null,
        firstAirDateLte: String? = null,
    ): ApiResponse<TmdbShowResult>

    /**
     * Get a list of TV shows ordered by popularity.
     *
     * @param page Page number
     */
    public suspend fun getPopularShows(page: Long): ApiResponse<TmdbShowResult>

    /**
     * Get a list of TV shows ordered by rating.
     *
     * @param page Page number
     */
    public suspend fun getTopRatedShows(page: Long): ApiResponse<TmdbShowResult>

    /**
     * Get the trending TV shows on TMDB for the day or week.
     *
     * @param timeWindow Default: Day
     */
    public suspend fun getTrendingShows(timeWindow: String): ApiResponse<TmdbShowResult>

    /**
     * Get the trending TV shows on TMDB for the day or week.
     *
     * @param year:
     */
    public suspend fun getUpComingShows(
        year: Int,
        page: Long,
        sortBy: String = DEFAULT_SORT_ORDER,
    ): ApiResponse<TmdbShowResult>

    /**
     * Get upcoming shows in a given date range. Eg. 4 weeks, 6 months.
     *
     * @param page Page number
     * @param firstAirDate Start range date range 2023-11-01
     * @param lastAirDate End range date range 2026-04-01
     * @param sortBy Default: popularity.desc.
     */
    public suspend fun getUpComingShows(
        page: Long,
        firstAirDate: String,
        lastAirDate: String,
        sortBy: String = DEFAULT_SORT_ORDER,
    ): ApiResponse<TmdbShowResult>

    /**
     * Search for TV shows by their original, translated and also known as names.
     *
     * @param query Search query
     */
    public suspend fun searchShows(query: String): ApiResponse<TmdbShowResult>

    /**
     * Get the list of official genres for TV shows.
     */
    public suspend fun getShowGenres(): ApiResponse<TmdbGenreResult>

    /**
     * Get the cast and crew for a TV show.
     *
     * @param tmdbId TMDB show ID
     */
    public suspend fun getShowCredits(tmdbId: Long): ApiResponse<CreditsResponse>
}
