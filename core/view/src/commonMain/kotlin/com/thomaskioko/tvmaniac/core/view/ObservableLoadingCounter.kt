package com.thomaskioko.tvmaniac.core.view

import com.thomaskioko.tvmaniac.core.logger.Logger
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class ObservableLoadingCounter {
    private val count = atomic(0)
    private val loadingState = MutableStateFlow(count.value)

    val observable: Flow<Boolean>
        get() = loadingState.map { it > 0 }.distinctUntilChanged()

    fun addLoader() {
        loadingState.value = count.incrementAndGet()
    }

    fun removeLoader() {
        loadingState.value = count.decrementAndGet()
    }
}

fun Flow<InvokeStatus>.onEachStatus(
    counter: ObservableLoadingCounter,
    logger: Logger? = null,
    uiMessageManager: UiMessageManager? = null,
    sourceId: String? = null,
): Flow<InvokeStatus> = onEach { status ->
    when (status) {
        InvokeStarted -> counter.addLoader()
        InvokeSuccess -> counter.removeLoader()
        is InvokeError -> {
            logger?.error("@InvokeError", status.throwable.message ?: "Unknown error")
            uiMessageManager?.emitMessageCombined(status.throwable, sourceId)
            counter.removeLoader()
        }
    }
}

suspend inline fun Flow<InvokeStatus>.collectStatus(
    counter: ObservableLoadingCounter,
    logger: Logger? = null,
    uiMessageManager: UiMessageManager? = null,
    sourceId: String? = null,
): Unit = onEachStatus(counter, logger, uiMessageManager, sourceId).collect()
