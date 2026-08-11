package com.thomaskioko.tvmaniac.watchlistprefs.testing

import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.watchlistprefs.api.WatchlistPrefsRepository
import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeWatchlistPrefsRepository : WatchlistPrefsRepository {

    private val listStyleFlow = MutableStateFlow(ListStyle.GRID)
    private val sortOptionFlow = MutableStateFlow(WatchlistSortOption.ADDED_DESC)

    public fun setListStyle(listStyle: ListStyle) {
        listStyleFlow.value = listStyle
    }

    public fun setSortOption(sortOption: WatchlistSortOption) {
        sortOptionFlow.value = sortOption
    }

    override fun observeListStyle(): Flow<ListStyle> = listStyleFlow

    override suspend fun saveListStyle(listStyle: ListStyle) {
        listStyleFlow.value = listStyle
    }

    override fun observeSortOption(): Flow<WatchlistSortOption> = sortOptionFlow.asStateFlow()

    override suspend fun saveSortOption(sortOption: WatchlistSortOption) {
        sortOptionFlow.value = sortOption
    }
}
