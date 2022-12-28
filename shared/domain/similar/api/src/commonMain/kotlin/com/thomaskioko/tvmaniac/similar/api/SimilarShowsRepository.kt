package com.thomaskioko.tvmaniac.similar.api

import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import kotlinx.coroutines.flow.Flow

interface SimilarShowsRepository {

    fun observeSimilarShows(traktId: Int): Flow<Either<Failure, List<SelectSimilarShows>>>
}
