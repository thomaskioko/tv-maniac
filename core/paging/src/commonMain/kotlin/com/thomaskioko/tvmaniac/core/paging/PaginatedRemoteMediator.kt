package com.thomaskioko.tvmaniac.core.paging

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.thomaskioko.tvmaniac.core.logger.CrashReportKeys
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import kotlinx.coroutines.CancellationException

public class PaginatedRemoteMediator<EM : Any>(
    private val logger: Logger,
    private val source: String,
    private val fetch: suspend (page: Long) -> FetchResult,
) : RemoteMediator<Int, EM>() {

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
                lastItem?.page?.plus(1) ?: 1
            }
        }
    }

    private suspend fun fetchPage(page: Long): MediatorResult {
        return try {
            when (val result = fetch(page)) {
                is FetchResult.Success ->
                    MediatorResult.Success(endOfPaginationReached = result.endOfPaginationReached)
                is FetchResult.Error -> {
                    reportFailure(result.error)
                    MediatorResult.Error(result.error)
                }
                is FetchResult.NoFetch ->
                    MediatorResult.Success(endOfPaginationReached = false)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            reportFailure(e)
            MediatorResult.Error(e)
        }
    }

    private fun reportFailure(throwable: Throwable) {
        logger.error(LOG_TAG, "Paging fetch failed", throwable, mapOf(CrashReportKeys.SOURCE to source))
    }

    private companion object {
        const val LOG_TAG: String = "PaginatedRemoteMediator"
    }
}

public sealed class FetchResult {
    public data class Success(val endOfPaginationReached: Boolean) : FetchResult()

    public data class Error(val error: Throwable) : FetchResult()

    public data object NoFetch : FetchResult()
}
