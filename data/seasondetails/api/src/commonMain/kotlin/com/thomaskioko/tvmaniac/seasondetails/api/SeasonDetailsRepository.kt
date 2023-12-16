package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsRepository {

    suspend fun fetchSeasonDetails(
        param: SeasonDetailsParam,
    ): SeasonDetailsWithEpisodes

    fun observeSeasonDetailsStream(
        param: SeasonDetailsParam,
    ): Flow<Either<Failure, SeasonDetailsWithEpisodes>>
}
