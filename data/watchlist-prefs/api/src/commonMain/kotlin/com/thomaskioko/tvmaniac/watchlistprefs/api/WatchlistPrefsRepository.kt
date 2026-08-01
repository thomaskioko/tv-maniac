package com.thomaskioko.tvmaniac.watchlistprefs.api

import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption
import kotlinx.coroutines.flow.Flow

public interface WatchlistPrefsRepository {

    public fun observeListStyle(): Flow<ListStyle>

    public suspend fun saveListStyle(listStyle: ListStyle)

    public fun observeSortOption(): Flow<WatchlistSortOption>

    public suspend fun saveSortOption(sortOption: WatchlistSortOption)
}
