package com.thomaskioko.tvmaniac.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface Cancelable {
  fun cancel()
}

open class FlowWrapper<T>(private val flow: Flow<T>) {

  fun collect(consumer: (T) -> Unit): Cancelable {
    val scope = CoroutineScope(Dispatchers.Main.immediate)

    flow.onEach { consumer(it) }.launchIn(scope)

    return object : Cancelable {
      override fun cancel() {
        scope.cancel()
      }
    }
  }
}
