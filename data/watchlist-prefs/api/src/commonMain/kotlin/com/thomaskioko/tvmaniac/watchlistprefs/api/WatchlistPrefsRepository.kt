package com.thomaskioko.tvmaniac.watchlistprefs.api

import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption
import kotlinx.coroutines.flow.Flow

public interface WatchlistPrefsRepository {

    public fun observeListStyle(): Flow<Boolean>

    public suspend fun saveListStyle(isGridMode: Boolean)

    public fun observeSortOption(): Flow<WatchlistSortOption>

    public suspend fun saveSortOption(sortOption: WatchlistSortOption)
}
