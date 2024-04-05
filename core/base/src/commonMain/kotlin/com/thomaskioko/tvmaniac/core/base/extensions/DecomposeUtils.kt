package com.thomaskioko.tvmaniac.core.base.extensions

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

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
