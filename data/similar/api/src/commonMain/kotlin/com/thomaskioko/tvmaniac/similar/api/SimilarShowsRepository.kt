package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface SimilarShowsRepository {

    suspend fun fetchSimilarShows(traktId: Long): List<SimilarShows>

    fun observeSimilarShows(traktId: Long): Flow<Either<Failure, List<SimilarShows>>>
}
