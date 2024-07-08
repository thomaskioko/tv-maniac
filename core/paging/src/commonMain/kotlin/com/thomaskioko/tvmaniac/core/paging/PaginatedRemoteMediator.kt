package com.thomaskioko.tvmaniac.core.paging

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.CancellationException

/**
 * Generic RemoteMediator for loading remote data into a database, then fetching it from the
 * database.
 *
 * @param fetch Executes the remote fetch.
 * @param EM Entity model.
 */
class PaginatedRemoteMediator<EM : Any>(
  private val fetch: suspend (page: Long) -> Unit,
) : RemoteMediator<Int, EM>() {
  override suspend fun load(loadType: LoadType, state: PagingState<Int, EM>): MediatorResult {
    val nextPage: Long =
      when (loadType) {
        LoadType.REFRESH -> 1
        LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
        LoadType.APPEND -> {
          val lastItem =
            state.lastItemOrNull() as? ShowEntity
              ?: return MediatorResult.Success(endOfPaginationReached = true)
          lastItem.page + 1
        }
        else -> error("Unknown LoadType: $loadType")
      }

    return try {
      fetch(nextPage)
      MediatorResult.Success(endOfPaginationReached = false)
    } catch (cancellationException: CancellationException) {
      throw cancellationException
    } catch (throwable: Throwable) {
      MediatorResult.Error(throwable)
    }
  }
}
