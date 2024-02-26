package com.thomaskioko.tvmaniac.core.networkutil.paging

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.LoadType
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import kotlinx.coroutines.CancellationException

/**
 * Generic RemoteMediator for loading remote data into a database, then fetching it from the
 * database.
 *
 * @param getLastPage Get the last stored page.
 * @param deleteLocalEntity Drops the relevant local entities.
 * @param fetch Executes the remote fetch.
 * @param EM Entity model.
 */
@OptIn(ExperimentalPagingApi::class)
class PaginatedRemoteMediator<EM : Any>(
  private val getLastPage: suspend () -> Long?,
  private val deleteLocalEntity: suspend () -> Unit,
  private val fetch: suspend (page: Long) -> Unit,
) : RemoteMediator<Int, EM>() {

  override suspend fun initialize(): InitializeAction {
    return if (getLastPage() == null) {
      InitializeAction.LAUNCH_INITIAL_REFRESH
    } else {
      InitializeAction.SKIP_INITIAL_REFRESH
    }
  }

  override suspend fun load(loadType: LoadType, state: PagingState<Int, EM>): MediatorResult {
    return try {
      val nextPage: Long =
        when (loadType) {
          LoadType.REFRESH -> {
            if (getLastPage() != null) {
              deleteLocalEntity()
            }
            0
          }
          LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
          LoadType.APPEND -> {
            val lastPage = getLastPage()
            val lastItem = state.lastItemOrNull()

            if (lastPage == lastItem) {
              return MediatorResult.Success(endOfPaginationReached = true)
            }

            if (lastPage != null) lastPage + 1 else 0
          }
        }

      fetch(nextPage)
      MediatorResult.Success(endOfPaginationReached = false)
    } catch (cancellationException: CancellationException) {
      throw cancellationException
    } catch (throwable: Throwable) {
      MediatorResult.Error(throwable)
    }
  }
}
