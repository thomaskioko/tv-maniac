package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface SeasonsRepository {

    suspend fun getSeasons(traktId: Long): List<Seasons>
    fun observeSeasonsStoreResponse(traktId: Long): Flow<Either<Failure, List<Seasons>>>
}
