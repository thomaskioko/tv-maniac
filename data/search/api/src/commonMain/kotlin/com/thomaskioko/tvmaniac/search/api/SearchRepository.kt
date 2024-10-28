package com.thomaskioko.tvmaniac.search.api

import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
  suspend fun search(query: String): Flow<Either<Failure, List<ShowEntity>>>
}
