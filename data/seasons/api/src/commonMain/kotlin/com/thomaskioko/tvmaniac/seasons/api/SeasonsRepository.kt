package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.core.db.SeasonsByShowId
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface SeasonsRepository {

    suspend fun fetchSeasonsByShowId(traktId: Long): List<SeasonsByShowId>
    fun observeSeasonsByShowId(traktId: Long): Flow<Either<Failure, List<SeasonsByShowId>>>
}
