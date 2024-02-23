package com.thomaskioko.tvmaniac.tmdb.api

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbSeasonDetailsResponse

interface TmdbSeasonDetailsNetworkDataSource {

  /**
   * Query the details of a TV season.
   *
   * @param id TV show id
   * @param seasonNumber Season number
   */
  suspend fun getSeasonDetails(id: Long, seasonNumber: Long): ApiResponse<TmdbSeasonDetailsResponse>
}
