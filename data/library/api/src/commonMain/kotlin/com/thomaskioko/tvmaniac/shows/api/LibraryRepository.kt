package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.LibraryShows
import com.thomaskioko.tvmaniac.core.db.library.SearchShows
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

  fun observeLibrary(): Flow<Either<Failure, List<LibraryShows>>>
  fun searchWatchlistByQuery(query: String): Flow<Either<Failure, List<SearchShows>>>

  suspend fun updateLibrary(traktId: Long, addToLibrary: Boolean)
}
