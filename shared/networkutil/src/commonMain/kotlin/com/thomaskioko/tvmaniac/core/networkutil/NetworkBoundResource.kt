package com.thomaskioko.tvmaniac.core.networkutil

import com.thomaskioko.tvmaniac.util.ExceptionHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

inline fun <ResultType, RequestType> networkBoundResult(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType?) -> Boolean = { true },
    exceptionHandler: ExceptionHandler,
    coroutineDispatcher: CoroutineDispatcher
) = flow<Either<Failure, ResultType>> {

    val data = query().first()

    if (shouldFetch(data)) {
        try {
            saveFetchResult(fetch())
            emit(Either.Right(query().first()))
        } catch (e: Exception) {
            emit(Either.Left(DefaultError(exceptionHandler.resolveError(e))))
        }
    } else {
        emit(Either.Right(query().first()))
    }
}.catch {
    emit(Either.Left(DefaultError(exceptionHandler.resolveError(it))))
}.flowOn(coroutineDispatcher)