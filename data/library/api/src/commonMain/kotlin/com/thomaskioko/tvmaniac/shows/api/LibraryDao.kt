package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.Library
import com.thomaskioko.tvmaniac.core.db.LibraryShows
import com.thomaskioko.tvmaniac.core.db.SearchWatchlist
import kotlinx.coroutines.flow.Flow

interface LibraryDao {

  fun upsert(watchlist: Library)

  fun upsert(watchedShowList: List<Library>)

  fun getShowsInLibrary(): List<LibraryShows>

  fun observeShowsInLibrary(): Flow<List<LibraryShows>>

  fun observeWatchlistByQuery(query: String): Flow<List<SearchWatchlist>>

  fun delete(traktId: Long)
}
