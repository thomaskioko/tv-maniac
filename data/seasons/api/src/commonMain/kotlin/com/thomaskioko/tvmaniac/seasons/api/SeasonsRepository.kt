package com.thomaskioko.tvmaniac.seasons.api

import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface SeasonsRepository {
    suspend fun fetchSeasonsByShowId(id: Long): List<ShowSeasons>
    fun observeSeasonsByShowId(id: Long): Flow<Either<Failure, List<ShowSeasons>>>
}
