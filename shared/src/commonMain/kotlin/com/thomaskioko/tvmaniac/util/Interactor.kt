package com.thomaskioko.tvmaniac.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class Interactor<Params, Type>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    protected abstract fun run(params: Params): Flow<DomainResultState<Type>>

    operator fun invoke(params: Params): Flow<DomainResultState<Type>> =
        run(params).flowOn(coroutineDispatcher)
}

operator fun <Type> Interactor<Unit, Type>.invoke(): Flow<DomainResultState<Type>> = this(Unit)
