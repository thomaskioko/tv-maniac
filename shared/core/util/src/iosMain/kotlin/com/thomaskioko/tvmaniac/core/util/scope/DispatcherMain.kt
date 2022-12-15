package com.thomaskioko.tvmaniac.core.util.scope

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual class DispatcherMain {
    actual val main: CoroutineDispatcher = Dispatchers.Main
}