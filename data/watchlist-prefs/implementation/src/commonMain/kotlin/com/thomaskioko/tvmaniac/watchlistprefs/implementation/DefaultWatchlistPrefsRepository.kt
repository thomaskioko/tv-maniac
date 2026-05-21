package com.thomaskioko.tvmaniac.watchlistprefs.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.watchlistprefs.api.WatchlistPrefsRepository
import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchlistPrefsRepository(
    private val datastoreRepository: DatastoreRepository,
) : WatchlistPrefsRepository {

    override fun observeListStyle(): Flow<Boolean> {
        return datastoreRepository.observeListStyle().map { listStyle ->
            listStyle == ListStyle.GRID
        }
    }

    override suspend fun saveListStyle(isGridMode: Boolean) {
        val listStyle = if (isGridMode) ListStyle.GRID else ListStyle.LIST
        datastoreRepository.saveListStyle(listStyle)
    }

    override fun observeSortOption(): Flow<WatchlistSortOption> =
        datastoreRepository.observeWatchlistSortOption().map { name ->
            WatchlistSortOption.entries.find { it.name == name }
                ?: WatchlistSortOption.ADDED_DESC
        }

    override suspend fun saveSortOption(sortOption: WatchlistSortOption) {
        datastoreRepository.saveWatchlistSortOption(sortOption.name)
    }
}
