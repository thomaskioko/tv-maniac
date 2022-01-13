package com.thomaskioko.tvmaniac.shared.core

import kotlinx.coroutines.CoroutineDispatcher

expect class MainDispatcher {
    val main: CoroutineDispatcher
}
