package com.thomaskioko.tvmaniac.core

import kotlinx.coroutines.CoroutineDispatcher

expect class MainDispatcher {
    val main: CoroutineDispatcher
}
