package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.SeasonEpisodeDetailsById
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsRepository {

    suspend fun fetchSeasonDetails(traktId: Long): List<SeasonEpisodeDetailsById>

    fun observeSeasonDetailsStream(traktId: Long): Flow<Either<Failure, List<SeasonEpisodeDetailsById>>>
}
