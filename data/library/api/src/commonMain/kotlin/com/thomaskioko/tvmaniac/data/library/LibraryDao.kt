package com.thomaskioko.tvmaniac.data.library

import com.thomaskioko.tvmaniac.db.LibraryShows
import kotlinx.coroutines.flow.Flow

public interface LibraryDao {

    public fun observeLibrary(followedOnly: Boolean): Flow<List<LibraryShows>>

    public fun searchLibrary(query: String): Flow<List<LibraryShows>>
}
