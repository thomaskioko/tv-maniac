package com.thomaskioko.tvmaniac.core.base.model

import kotlinx.coroutines.CoroutineDispatcher

public data class AppCoroutineDispatchers(
    val io: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val databaseWrite: CoroutineDispatcher,
    val databaseRead: CoroutineDispatcher,
    val main: CoroutineDispatcher,
)
