package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.Library
import com.thomaskioko.tvmaniac.core.db.LibraryShows
import kotlinx.coroutines.flow.Flow

interface LibraryDao {

    fun upsert(watchlist: Library)

    fun upsert(watchedShowList: List<Library>)

    fun getShowsInLibrary(): List<LibraryShows>

    fun observeShowsInLibrary(): Flow<List<LibraryShows>>

    fun delete(traktId: Long)
}
