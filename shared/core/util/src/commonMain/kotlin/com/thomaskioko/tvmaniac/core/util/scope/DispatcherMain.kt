package com.thomaskioko.tvmaniac.core.util.scope

import kotlinx.coroutines.CoroutineDispatcher

expect class DispatcherMain {
    val main: CoroutineDispatcher
}
