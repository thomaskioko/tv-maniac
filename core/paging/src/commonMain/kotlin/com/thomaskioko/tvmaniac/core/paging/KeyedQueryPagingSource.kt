package com.thomaskioko.tvmaniac.core.paging

import androidx.paging.PagingState
import app.cash.sqldelight.Query
import app.cash.sqldelight.SuspendingTransacter
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.TransacterBase
import app.cash.sqldelight.TransactionCallbacks
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class KeyedQueryPagingSource<Key : Any, RowType : Any>(
    private val queryProvider: (beginInclusive: Key, endExclusive: Key?) -> Query<RowType>,
    private val pageBoundariesProvider: (anchor: Key?, limit: Long) -> Query<Key>,
    private val transacter: TransacterBase,
    private val context: CoroutineContext,
) : QueryPagingSource<Key, RowType>() {

    private var pageBoundaries: List<Key>? = null
    override val jumpingSupported: Boolean
        get() = false

    override fun getRefreshKey(state: PagingState<Key, RowType>): Key? {
        val boundaries = pageBoundaries ?: return null
        val last = state.pages.lastOrNull() ?: return null
        val keyIndexFromNext = last.nextKey?.let { boundaries.indexOf(it) - 1 }
        val keyIndexFromPrev = last.prevKey?.let { boundaries.indexOf(it) + 1 }
        val keyIndex = keyIndexFromNext ?: keyIndexFromPrev ?: return null

        return boundaries.getOrNull(keyIndex)
    }

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, RowType> {
        return withContext(context) {
            try {
                val getPagingSourceLoadResult: TransactionCallbacks.() -> LoadResult<Key, RowType> = {
                    val boundaries = pageBoundaries ?: pageBoundariesProvider(params.key, params.loadSize.toLong())
                        .executeAsList()
                        .also {
                            pageBoundaries = it
                        }

                    val key = params.key ?: boundaries.first()

                    require(key in boundaries)

                    val keyIndex = boundaries.indexOf(key)
                    val previousKey = boundaries.getOrNull(keyIndex - 1)
                    val nextKey = boundaries.getOrNull(keyIndex + 1)
                    val results = queryProvider(key, nextKey).also { currentQuery = it }.executeAsList()

                    LoadResult.Page(
                        data = results,
                        prevKey = previousKey,
                        nextKey = nextKey,
                    )
                }
                when (transacter) {
                    is Transacter ->
                        transacter.transactionWithResult(bodyWithReturn = getPagingSourceLoadResult)

                    is SuspendingTransacter ->
                        transacter.transactionWithResult(bodyWithReturn = getPagingSourceLoadResult)
                }
            } catch (e: Exception) {
                if (e is IllegalArgumentException) throw e
                LoadResult.Error<Key, RowType>(e)
            }
        }
    }
}
