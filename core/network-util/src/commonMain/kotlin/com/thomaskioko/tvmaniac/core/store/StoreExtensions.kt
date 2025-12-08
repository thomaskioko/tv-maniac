@file:Suppress("NOTHING_TO_INLINE")

package com.thomaskioko.tvmaniac.core.store

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder

inline fun <Key : Any, Local : Any, Output : Any> storeBuilder(
    fetcher: Fetcher<Key, Local>,
    sourceOfTruth: SourceOfTruth<Key, Local, Output>,
): StoreBuilder<Key, Output> = StoreBuilder.from(fetcher, sourceOfTruth)

inline fun <Key : Any, reified Output : Any> apiFetcher(
    crossinline apiCall: suspend (Key) -> ApiResponse<Output>,
): Fetcher<Key, Output> = Fetcher.ofResult { key: Key ->
    when (val response = apiCall(key)) {
        is ApiResponse.Success -> FetcherResult.Data(response.body)
        is ApiResponse.Error.GenericError -> FetcherResult.Error.Message("API Error: ${response.message ?: response.errorMessage}")
        is ApiResponse.Error.HttpError -> FetcherResult.Error.Message("HTTP Error ${response.code}: ${response.errorMessage}")
        is ApiResponse.Error.SerializationError -> FetcherResult.Error.Message("Serialization Error: ${response.message ?: response.errorMessage}")
    }
}

fun <Key : Any, Local : Any, Output : Any> SourceOfTruth<Key, Local, Output>.usingDispatchers(
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
