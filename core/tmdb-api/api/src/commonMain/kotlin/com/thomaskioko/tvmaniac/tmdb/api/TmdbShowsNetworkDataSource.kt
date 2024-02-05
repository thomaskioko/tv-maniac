package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.util.model.ApiResponse

const val DEFAULT_API_PAGE: Long = 1
const val DEFAULT_SORT_ORDER: String = "popularity.desc"

interface TmdbShowsNetworkDataSource {

  /**
   * Get a list of TV shows airing today.
   *
   * @param page Page number
   */
  suspend fun getAiringToday(page: Long): ApiResponse<TmdbShowResult>

  /**
   * Find TV shows using over 30 filters and sort options. Available filter options asc & desc
   * popularity.asc, primary_release_date.desc, vote_average.desc, vote_count.desc
   *
   * @param page Page number
   * @param sortBy Default: popularity.desc.
   */
  suspend fun getDiscoverShows(
    page: Long,
    sortBy: String = DEFAULT_SORT_ORDER,
  ): ApiResponse<TmdbShowResult>

  /**
   * Get a list of TV shows ordered by popularity.
   *
   * @param page Page number
   */
  suspend fun getPopularShows(page: Long): ApiResponse<TmdbShowResult>

  /**
   * Get a list of TV shows ordered by rating.
   *
   * @param page Page number
   */
  suspend fun getTopRatedShows(page: Long): ApiResponse<TmdbShowResult>

  /**
   * Get the trending TV shows on TMDB for the day or week.
   *
   * @param timeWindow Default: Day
   */
  suspend fun getTrendingShows(timeWindow: String): ApiResponse<TmdbShowResult>

  /**
   * Get the trending TV shows on TMDB for the day or week.
   *
   * @param year:
   */
  suspend fun getUpComingShows(
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
  suspend fun getUpComingShows(
    page: Long,
    firstAirDate: String,
    lastAirDate: String,
    sortBy: String = DEFAULT_SORT_ORDER,
  ): ApiResponse<TmdbShowResult>
}
