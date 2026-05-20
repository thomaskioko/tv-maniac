package com.thomaskioko.tvmaniac.watchlistprefs.testing

import com.thomaskioko.tvmaniac.watchlistprefs.api.WatchlistPrefsRepository
import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeWatchlistPrefsRepository : WatchlistPrefsRepository {

    private val listStyleFlow = MutableStateFlow(true)
    private val sortOptionFlow = MutableStateFlow(WatchlistSortOption.ADDED_DESC)

    public fun setSortOption(sortOption: WatchlistSortOption) {
        sortOptionFlow.value = sortOption
    }

    override fun observeListStyle(): Flow<Boolean> = listStyleFlow

    override suspend fun saveListStyle(isGridMode: Boolean) {
        listStyleFlow.value = isGridMode
    }

    override fun observeSortOption(): Flow<WatchlistSortOption> = sortOptionFlow.asStateFlow()

    override suspend fun saveSortOption(sortOption: WatchlistSortOption) {
        sortOptionFlow.value = sortOption
    }
}
