package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.StoreReadRequest

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultWatchlistRepository(
    private val watchlistDao: WatchlistDao,
    private val watchlistMetadataStore: WatchlistMetadataStore,
    private val datastoreRepository: DatastoreRepository,
) : WatchlistRepository {

    override suspend fun updateLibrary(id: Long, addToLibrary: Boolean) {
        when {
            addToLibrary -> watchlistDao.upsert(id)
            else -> watchlistDao.delete(id)
        }
    }

    override fun observeWatchlist(): Flow<List<Watchlists>> =
        watchlistDao.observeShowsInWatchlist().distinctUntilChanged()

    override fun observeUnSyncedItems(): Flow<Unit> {
        return watchlistDao.observeUnSyncedWatchlist().flatMapMerge { ids ->
            flow {
                ids.forEach { id ->
                    watchlistMetadataStore.stream(StoreReadRequest.fresh(id)).collect()
                    emit(Unit)
                }
            }
        }
    }

    override fun searchWatchlistByQuery(query: String): Flow<List<SearchWatchlist>> {
        return watchlistDao.observeWatchlistByQuery(query)
    }

    override fun observeListStyle(): Flow<Boolean> {
        return datastoreRepository.observeListStyle().map { listStyle ->
            listStyle == ListStyle.GRID
        }
    }

    override suspend fun saveListStyle(isGridMode: Boolean) {
        val listStyle = if (isGridMode) ListStyle.GRID else ListStyle.LIST
        datastoreRepository.saveListStyle(listStyle)
    }
}
