package com.thomaskioko.tvmaniac.data.library

import com.thomaskioko.tvmaniac.data.library.model.LibraryItem
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

public interface LibraryRepository {

    public fun observeLibrary(
        query: String,
        sortOption: LibrarySortOption,
        followedOnly: Boolean,
    ): Flow<List<LibraryItem>>

    public fun observeListStyle(): Flow<ListStyle>

    public suspend fun saveListStyle(listStyle: ListStyle)

    public fun observeSortOption(): Flow<LibrarySortOption>

    public suspend fun saveSortOption(sortOption: LibrarySortOption)

    public suspend fun syncLibrary(forceRefresh: Boolean = false)

    public suspend fun syncPendingFollowedShows()

    public suspend fun needsSync(expiry: Duration): Boolean

    public suspend fun countPendingFollowedShows(): Long
}
