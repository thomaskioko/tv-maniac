package com.thomaskioko.tvmaniac.core.networkutil

import com.thomaskioko.tvmaniac.core.networkutil.model.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Either.Left
import com.thomaskioko.tvmaniac.core.networkutil.model.Either.Right
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import org.mobilenativefoundation.store.store5.StoreReadResponse

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<StoreReadResponse<T>>.mapResult(): Flow<Either<Failure, T>> =
  distinctUntilChanged().filterForResult().flatMapLatest { result ->
    when (val data = result.dataOrNull()) {
      null -> flowOf(Left(DefaultError(result.errorMessageOrNull())))
      else -> flowOf(Right(data))
    }
  }

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<StoreReadResponse<T>>.mapResult(cachedData: T): Flow<Either<Failure, T>> =
  distinctUntilChanged().filterForResult().flatMapLatest { result ->
    when (val data = result.dataOrNull()) {
      result.errorMessageOrNull() -> flowOf(Left(DefaultError(result.errorMessageOrNull())))
      null -> flowOf(Right(cachedData))
      else -> flowOf(Right(data))
    }
  }

fun <T> Flow<StoreReadResponse<T>>.filterForResult(): Flow<StoreReadResponse<T>> = filterNot {
  it is StoreReadResponse.Loading || it is StoreReadResponse.NoNewData
}
