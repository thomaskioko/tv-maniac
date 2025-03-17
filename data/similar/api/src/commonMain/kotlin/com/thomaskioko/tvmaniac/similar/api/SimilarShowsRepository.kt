package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow

interface SimilarShowsRepository {
  fun observeSimilarShows(
    id: Long,
    forceReload: Boolean = false
  ): Flow<Either<Failure, List<SimilarShows>>>
}
