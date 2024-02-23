package com.thomaskioko.tvmaniac.core.base.extensions

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.subscribe
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * This helper implementation in from Cofetti Kmp App See
 * https://github.com/joreilly/Confetti/blob/fb832c2131b2f3e5276a1a3a30666aa571e1e17e/shared/src/commonMain/kotlin/dev/johnoreilly/confetti/decompose/DecomposeUtils.kt#L27
 */
fun LifecycleOwner.coroutineScope(
  context: CoroutineContext = Dispatchers.Main.immediate,
): CoroutineScope {
  val scope = CoroutineScope(context + SupervisorJob())
  lifecycle.doOnDestroy(scope::cancel)

  return scope
}

fun <T : Any> StateFlow<T>.asValue(
  lifecycle: Lifecycle,
  context: CoroutineContext = Dispatchers.Main.immediate,
): Value<T> =
  asValue(
    initialValue = value,
    lifecycle = lifecycle,
    context = context,
  )

fun <T : Any> Flow<T>.asValue(
  initialValue: T,
  lifecycle: Lifecycle,
  context: CoroutineContext = Dispatchers.Main.immediate,
): Value<T> {
  val value = MutableValue(initialValue)
  var scope: CoroutineScope? = null

  lifecycle.subscribe(
    onStart = { scope = CoroutineScope(context).apply { launch { collect { value.value = it } } } },
    onStop = {
      scope?.cancel()
      scope = null
    },
  )

  return value
}
