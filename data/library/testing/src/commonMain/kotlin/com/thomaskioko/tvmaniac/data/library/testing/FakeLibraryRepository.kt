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
    private val sortOptionFlow = MutableStateFlow(LibrarySortOption.ADDED_DESC)
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
                LibrarySortOption.RANK_ASC -> filtered
                LibrarySortOption.RANK_DESC -> filtered.reversed()
                LibrarySortOption.ADDED_DESC -> filtered.sortedByDescending { it.followedAt ?: 0L }
                LibrarySortOption.ADDED_ASC -> filtered.sortedBy { it.followedAt ?: Long.MAX_VALUE }
                LibrarySortOption.RELEASED_DESC -> filtered.sortedByDescending { it.year }
                LibrarySortOption.RELEASED_ASC -> filtered.sortedBy { it.year }
                LibrarySortOption.TITLE_ASC -> filtered.sortedBy { it.title.lowercase() }
                LibrarySortOption.TITLE_DESC -> filtered.sortedByDescending { it.title.lowercase() }
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
