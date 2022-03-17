package com.thomaskioko.tvmaniac.shared.core.ui

import kotlinx.coroutines.CoroutineDispatcher

expect class MainDispatcher {
    val main: CoroutineDispatcher
}
