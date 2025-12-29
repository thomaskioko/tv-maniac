package com.thomaskioko.tvmaniac.core.base.extensions

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.CoroutineContext

/**
 * This helper implementation in from Cofetti Kmp App See
 * https://github.com/joreilly/Confetti/blob/fb832c2131b2f3e5276a1a3a30666aa571e1e17e/shared/src/commonMain/kotlin/dev/johnoreilly/confetti/decompose/DecomposeUtils.kt#L27
 */
public fun LifecycleOwner.coroutineScope(
    context: CoroutineContext = Dispatchers.Main.immediate,
): CoroutineScope {
    val scope = CoroutineScope(context + SupervisorJob())
    lifecycle.doOnDestroy(scope::cancel)

    return scope
}

/**
 * Creates a Main [CoroutineScope] instance tied to the lifecycle of this [ComponentContext].
 */
public fun LifecycleOwner.componentCoroutineScope(): CoroutineScope =
    MainScope().also { coroutineScope ->
        lifecycle.doOnDestroy { coroutineScope.cancel() }
    }

/**
 * Converts this Decompose [Value] to Kotlin [StateFlow].
 */
public fun <T : Any> Value<T>.asStateFlow(coroutineScope: CoroutineScope): StateFlow<T> = asFlow()
    .stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = value,
    )

/**
 * Converts this Decompose [Value] to Kotlin [Flow].
 */
public fun <T : Any> Value<T>.asFlow(): Flow<T> = callbackFlow {
    val cancellation = subscribe { value ->
        trySendBlocking(value)
    }

    awaitClose {
        cancellation.cancel()
    }
}
