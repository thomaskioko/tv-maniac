package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.Season_images
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsRepository {

  suspend fun fetchSeasonDetails(param: SeasonDetailsParam): SeasonDetailsWithEpisodes

  suspend fun fetchSeasonImages(id: Long): List<Season_images>

  fun observeSeasonDetails(
    param: SeasonDetailsParam,
  ): Flow<Either<Failure, SeasonDetailsWithEpisodes>>

  fun observeSeasonImages(id: Long): Flow<List<Season_images>>
}
