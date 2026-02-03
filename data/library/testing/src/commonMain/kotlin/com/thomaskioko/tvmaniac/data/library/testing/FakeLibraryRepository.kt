package com.thomaskioko.tvmaniac.data.library.testing

import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.data.library.model.LibraryItem
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration

public class FakeLibraryRepository : LibraryRepository {

    private val libraryItemsFlow = MutableStateFlow<List<LibraryItem>>(emptyList())
    private val isGridModeFlow = MutableStateFlow(true)
    private val sortOptionFlow = MutableStateFlow(LibrarySortOption.LAST_WATCHED_DESC)
    private var needsSyncResult = true

    public fun setLibraryItems(items: List<LibraryItem>) {
        libraryItemsFlow.value = items
    }

    public fun setIsGridMode(isGridMode: Boolean) {
        isGridModeFlow.value = isGridMode
    }

    public fun setSortOption(sortOption: LibrarySortOption) {
        sortOptionFlow.value = sortOption
    }

    public fun setNeedsSyncResult(value: Boolean) {
        needsSyncResult = value
    }

    override fun observeLibrary(
        query: String,
        sortOption: LibrarySortOption,
        followedOnly: Boolean,
    ): Flow<List<LibraryItem>> {
        return libraryItemsFlow.map { items ->
            var filtered = items

            if (query.isNotBlank()) {
                filtered = filtered.filter { it.title.contains(query, ignoreCase = true) }
            }

            if (followedOnly) {
                filtered = filtered.filter { it.isFollowed }
            }

            when (sortOption) {
                LibrarySortOption.LAST_WATCHED_DESC -> filtered.sortedByDescending { it.lastWatchedAt ?: it.followedAt ?: 0L }
                LibrarySortOption.LAST_WATCHED_ASC -> filtered.sortedBy { it.lastWatchedAt ?: it.followedAt ?: 0L }
                LibrarySortOption.NEW_EPISODES -> filtered.sortedByDescending { it.totalCount - it.watchedCount }
                LibrarySortOption.EPISODES_LEFT_DESC -> filtered.sortedByDescending { it.totalCount - it.watchedCount }
                LibrarySortOption.EPISODES_LEFT_ASC -> filtered.sortedBy { it.totalCount - it.watchedCount }
                LibrarySortOption.ALPHABETICAL -> filtered.sortedBy { it.title.lowercase() }
            }
        }
    }

    override fun observeListStyle(): Flow<Boolean> = isGridModeFlow.asStateFlow()

    override suspend fun saveListStyle(isGridMode: Boolean) {
        isGridModeFlow.value = isGridMode
    }

    override fun observeSortOption(): Flow<LibrarySortOption> = sortOptionFlow.asStateFlow()

    override suspend fun saveSortOption(sortOption: LibrarySortOption) {
        sortOptionFlow.value = sortOption
    }

    override suspend fun syncLibrary(forceRefresh: Boolean) {
    }

    override suspend fun needsSync(expiry: Duration): Boolean = needsSyncResult
}
