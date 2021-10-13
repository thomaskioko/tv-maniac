package com.thomaskioko.tvmaniac.util

import com.kuuurt.paging.multiplatform.helpers.dispatcher
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> Flow<T>.asCommonFlow(): CommonFlow<T> = CommonFlow(this)

class CommonFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    fun watch(block: (T) -> Unit): Closeable {
        val job = Job()
        onEach { block(it) }.launchIn(CoroutineScope(job + dispatcher()))

        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }
}