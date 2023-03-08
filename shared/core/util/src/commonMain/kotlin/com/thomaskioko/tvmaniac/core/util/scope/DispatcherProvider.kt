package com.thomaskioko.tvmaniac.core.util.scope

import kotlinx.coroutines.CoroutineDispatcher

expect class DispatcherProvider {
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}
