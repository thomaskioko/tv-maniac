package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
  suspend fun observeGenresWithShows(forceRefresh: Boolean = false): Flow<Either<Failure, List<ShowGenresEntity>>>
}
