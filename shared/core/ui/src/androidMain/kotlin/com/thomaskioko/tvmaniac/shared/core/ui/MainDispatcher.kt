package com.thomaskioko.tvmaniac.shared.core.ui

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual class MainDispatcher {
    actual val main: CoroutineDispatcher = Dispatchers.Main
}