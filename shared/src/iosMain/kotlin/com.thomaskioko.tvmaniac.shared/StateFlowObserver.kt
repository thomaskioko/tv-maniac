package com.thomaskioko.tvmaniac.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Suppress("unused") // Used in Swift
class StateFlowObserver<T>(private val stateFlow: StateFlow<T>) {
  private val job = SupervisorJob()
  private val scope = CoroutineScope(Dispatchers.Main.immediate + job)

  fun observe(observer: (T) -> Unit) {
    stateFlow.onEach { observer(it) }.flowOn(Dispatchers.Main.immediate).launchIn(scope)
  }

  fun unsubscribe() {
    job.cancelChildren()
  }
}
