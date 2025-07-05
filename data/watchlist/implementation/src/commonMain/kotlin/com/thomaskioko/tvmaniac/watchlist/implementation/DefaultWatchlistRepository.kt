package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.core.networkutil.NetworkExceptionHandler
import com.thomaskioko.tvmaniac.core.networkutil.model.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultWatchlistRepository(
    private val watchlistDao: WatchlistDao,
    private val watchlistMetadataStore: WatchlistMetadataStore,
    private val exceptionHandler: NetworkExceptionHandler,
    private val datastoreRepository: DatastoreRepository,
) : WatchlistRepository {

    override suspend fun updateLibrary(id: Long, addToLibrary: Boolean) {
        when {
            addToLibrary -> watchlistDao.upsert(id)
            else -> watchlistDao.delete(id)
        }
    }

    override fun observeWatchlist(): Flow<Either<Failure, List<Watchlists>>> =
        watchlistDao.observeShowsInWatchlist().distinctUntilChanged().map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }

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

    override fun searchWatchlistByQuery(query: String): Flow<Either<Failure, List<SearchWatchlist>>> {
        return watchlistDao.observeWatchlistByQuery(query).map { Either.Right(it) }
            .catch { Either.Left(DefaultError(exceptionHandler.resolveError(it))) }
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
