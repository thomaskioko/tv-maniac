package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.core.db.SeasonEpisodeDetailsById
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface SeasonDetailsRepository {

    suspend fun fetchSeasonDetails(traktId: Long): List<SeasonEpisodeDetailsById>

    fun observeSeasonDetailsStream(traktId: Long): Flow<Either<Failure, List<SeasonEpisodeDetailsById>>>
}
