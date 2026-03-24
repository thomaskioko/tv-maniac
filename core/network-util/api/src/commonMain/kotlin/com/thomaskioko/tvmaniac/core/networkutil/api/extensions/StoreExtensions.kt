@file:Suppress("NOTHING_TO_INLINE")

package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toSyncError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get

/**
 * Creates a [StoreBuilder] wired with the given [fetcher] and [sourceOfTruth].
 *
 * @param fetcher Network data source that fetches [Local] values by [Key].
 * @param sourceOfTruth Local persistence layer that reads [Output] and writes [Local] by [Key].
 * @return A [StoreBuilder] ready for further configuration (e.g. validation, caching policy).
 */
public inline fun <Key : Any, Local : Any, Output : Any> storeBuilder(
    fetcher: Fetcher<Key, Local>,
    sourceOfTruth: SourceOfTruth<Key, Local, Output>,
): StoreBuilder<Key, Output> = StoreBuilder.from(fetcher, sourceOfTruth)

/**
 * Creates a [Fetcher] that maps an [ApiResponse] to a [FetcherResult].
 *
 * - [ApiResponse.Success] → [FetcherResult.Data]
 * - [ApiResponse.Unauthenticated] → [FetcherResult.Error.Exception] with [AuthenticationException]
 * - Other error variants → [FetcherResult.Error.Message] with a descriptive string
 *
 * @param apiCall Suspend function that performs the network request for the given [Key].
 */
public inline fun <Key : Any, reified Output : Any> apiFetcher(
    crossinline apiCall: suspend (Key) -> ApiResponse<Output>,
): Fetcher<Key, Output> = Fetcher.ofResult { key: Key ->
    when (val response = apiCall(key)) {
        is ApiResponse.Success -> FetcherResult.Data(response.body)
        is ApiResponse.Unauthenticated -> FetcherResult.Error.Exception(
            AuthenticationException("Not authenticated"),
        )
        is ApiResponse.Error -> FetcherResult.Error.Exception(SyncException(response.toSyncError()))
    }
}

/**
 * Calls [Store.get] while silently ignoring [AuthenticationException].
 *
 * This handles the case where the user is not logged in and the endpoint requires
 * authentication. The request is skipped as a no-op rather than surfacing an error.
 *
 * Note: Token expiry for authenticated users is handled upstream by Ktor's Auth plugin,
 * which triggers [TraktAuthRepository.refreshTokens] and emits [AuthError.TokenExpired]
 * for the presentation layer to handle.
 */
public suspend fun <Key : Any, Output : Any> Store<Key, Output>.get(key: Key) {
    try {
        get(key)
    } catch (_: AuthenticationException) {
    }
}

/**
 * Calls [Store.fresh] (bypassing cache) while silently ignoring [AuthenticationException].
 *
 * This handles the case where the user is not logged in and the endpoint requires
 * authentication. The request is skipped as a no-op rather than surfacing an error.
 *
 * Note: Token expiry for authenticated users is handled upstream by Ktor's Auth plugin,
 * which triggers [TraktAuthRepository.refreshTokens] and emits [AuthError.TokenExpired]
 * for the presentation layer to handle.
 */
public suspend fun <Key : Any, Output : Any> Store<Key, Output>.fresh(key: Key) {
    try {
        fresh(key)
    } catch (_: AuthenticationException) {
    }
}

/**
 * Wraps this [SourceOfTruth] so that reads flow on [readDispatcher] and writes
 * (including deletes) execute on [writeDispatcher].
 *
 * @param readDispatcher Dispatcher used for [SourceOfTruth.reader] emissions.
 * @param writeDispatcher Dispatcher used for [SourceOfTruth.write], [SourceOfTruth.delete],
 *   and [SourceOfTruth.deleteAll].
 */
public fun <Key : Any, Local : Any, Output : Any> SourceOfTruth<Key, Local, Output>.usingDispatchers(
    readDispatcher: CoroutineDispatcher,
    writeDispatcher: CoroutineDispatcher,
): SourceOfTruth<Key, Local, Output> {
    val wrapped = this
    return object : SourceOfTruth<Key, Local, Output> {
        override fun reader(key: Key): Flow<Output?> = wrapped.reader(key).flowOn(readDispatcher)

        override suspend fun write(key: Key, value: Local) = withContext(writeDispatcher) {
            wrapped.write(key, value)
        }

        override suspend fun delete(key: Key) = withContext(writeDispatcher) {
            wrapped.delete(key)
        }

        override suspend fun deleteAll() = withContext(writeDispatcher) {
            wrapped.deleteAll()
        }
    }
}
