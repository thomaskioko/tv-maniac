package com.thomaskioko.tvmaniac.data.library

import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.db.LibraryShows
import com.thomaskioko.tvmaniac.db.WatchProvidersForShow
import kotlinx.coroutines.flow.Flow

public interface LibraryDao {

    public fun observeLibrary(
        sortOption: LibrarySortOption,
        followedOnly: Boolean,
    ): Flow<List<LibraryShows>>

    public fun searchLibrary(query: String): Flow<List<LibraryShows>>

    public fun getWatchProviders(tmdbId: Long): List<WatchProvidersForShow>
}
