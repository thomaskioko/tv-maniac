package com.thomaskioko.tvmaniac.core.store

import com.thomaskioko.tvmaniac.core.networkutil.model.DefaultError
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Either.Left
import com.thomaskioko.tvmaniac.core.networkutil.model.Either.Right
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.StoreReadResponse

fun <T> Flow<StoreReadResponse<T>>.mapToEither(): Flow<Either<Failure, T>> =
  distinctUntilChanged()
    .filterForResult()
    .map { result ->
      when (val data = result.dataOrNull()) {
        null -> Left(DefaultError(result.errorMessageOrNull()))
        else -> Right(data)
      }
    }

fun <T> Flow<StoreReadResponse<T>>.filterForResult(): Flow<StoreReadResponse<T>> = filterNot {
  it is StoreReadResponse.Loading || it is StoreReadResponse.NoNewData
}
