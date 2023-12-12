package com.thomaskioko.tvmaniac.util.extensions

import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import org.mobilenativefoundation.store.store5.StoreReadResponse

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<StoreReadResponse<T>>.mapResult(): Flow<Either<Failure, T>> =
    distinctUntilChanged()
        .flatMapLatest {
            val data = it.dataOrNull()
            if (data != null) {
                flowOf(Either.Right(data))
            } else {
                emptyFlow()
            }
        }
