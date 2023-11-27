package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.WatchedShow
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    fun observeLibrary(): Flow<Either<Failure, List<WatchedShow>>>

    suspend fun getLibraryShows(): List<WatchedShow>

    suspend fun updateLibrary(traktId: Long, addToLibrary: Boolean)

    suspend fun syncLibrary()
}
