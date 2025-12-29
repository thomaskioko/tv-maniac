package com.thomaskioko.tvmaniac.core.paging

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.CancellationException

/**
 * Generic RemoteMediator for loading remote data into a database, then fetching it from the
 * database.
 *
 * @param fetch Executes the remote fetch.
 * @param EM Entity model.
 */
public class PaginatedRemoteMediator<EM : Any>(private val fetch: suspend (page: Long) -> FetchResult) :
    RemoteMediator<Int, EM>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, EM>): MediatorResult {
        return when (val page = getNextPageNumber(loadType, state)) {
            null -> MediatorResult.Success(endOfPaginationReached = true)
            else -> fetchPage(page)
        }
    }

    private fun getNextPageNumber(loadType: LoadType, state: PagingState<Int, EM>): Long? {
        return when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> null
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull() as? ShowEntity
                lastItem?.page?.plus(1) ?: 1 // If lastItem is null, we start from page 1
            }
        }
    }

    private suspend fun fetchPage(page: Long): MediatorResult {
        return try {
            when (val result = fetch(page)) {
                is FetchResult.Success ->
                    MediatorResult.Success(endOfPaginationReached = result.endOfPaginationReached)
                is FetchResult.Error -> MediatorResult.Error(result.error)
                is FetchResult.NoFetch ->
                    MediatorResult.Success(endOfPaginationReached = false) // Changed this to false
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}

public sealed class FetchResult {
    public data class Success(val endOfPaginationReached: Boolean) : FetchResult()

    public data class Error(val error: Throwable) : FetchResult()

    public data object NoFetch : FetchResult()
}
