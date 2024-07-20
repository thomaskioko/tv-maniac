package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.Season_images
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsRepository {

  fun observeSeasonDetails(
    param: SeasonDetailsParam,
  ): Flow<Either<Failure, SeasonDetailsWithEpisodes>>

  fun observeSeasonImages(id: Long): Flow<List<Season_images>>
}
