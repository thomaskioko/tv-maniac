package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.LibraryShows
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    fun observeLibrary(): Flow<Either<Failure, List<LibraryShows>>>

    suspend fun getLibraryShows(): List<LibraryShows>

    suspend fun updateLibrary(traktId: Long, addToLibrary: Boolean)
}
