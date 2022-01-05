package com.thomaskioko.tvmaniac.datasource.repository.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType?>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { },
    crossinline shouldFetch: (ResultType?) -> Boolean = { true },
    coroutineDispatcher: CoroutineDispatcher
) = flow<Resource<ResultType>> {

    // check for data in database
    val data = query().firstOrNull()

    if (data != null) {
        // data is not null -> update loading status
        emit(Resource.loading(data))
    }

    if (shouldFetch(data)) {
        // Need to fetch data -> call backend
        val fetchResult = fetch()
        // got data from backend, store it in database
        saveFetchResult(fetchResult)
    }

    // load updated data from database (must not return null anymore)
    val updatedData = query().first()

    // emit updated data
    emit(Resource.success(updatedData))
}
    .onStart { emit(Resource.loading(null)) }
    .catch { exception ->
        onFetchFailed(exception)
        emit(Resource.error("Something went wrong! $exception", null))
    }
    .flowOn(coroutineDispatcher)
