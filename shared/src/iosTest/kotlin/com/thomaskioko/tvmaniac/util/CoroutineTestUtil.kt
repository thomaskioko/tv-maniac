package com.thomaskioko.tvmaniac.util

import kotlinx.coroutines.CoroutineScope

actual fun <T> runBlocking(block: suspend CoroutineScope.() -> T): T {
    return kotlinx.coroutines.runBlocking {
        block(this)
    }
}