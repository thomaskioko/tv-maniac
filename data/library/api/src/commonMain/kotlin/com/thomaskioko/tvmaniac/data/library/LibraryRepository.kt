package com.thomaskioko.tvmaniac.data.library

import com.thomaskioko.tvmaniac.data.library.model.LibraryItem
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
import kotlinx.coroutines.flow.Flow

public interface LibraryRepository {

    public fun observeLibrary(
        query: String,
        sortOption: LibrarySortOption,
        followedOnly: Boolean,
    ): Flow<List<LibraryItem>>

    public fun observeListStyle(): Flow<Boolean>

    public suspend fun saveListStyle(isGridMode: Boolean)

    public fun observeSortOption(): Flow<LibrarySortOption>

    public suspend fun saveSortOption(sortOption: LibrarySortOption)
}
