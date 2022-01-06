package com.thomaskioko.tvmaniac.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual class MainDispatcher {
    actual val main: CoroutineDispatcher = Dispatchers.Main
}
