package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.core.base.ActivityScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * In-memory [NavigationResultRegistry] implementation.
 *
 * Each [NavigationResultRequest.Key] is backed by a capacity-1 [Channel] with [BufferOverflow.DROP_OLDEST]
 * semantics: a newer delivery replaces any unconsumed prior value for the same key, and the first
 * collector consumes the value exactly once. Only one logical collector per key is supported, which
 * matches the per-destination lifecycle that drives navigation-for-result flows.
 *
 * The registry is activity-scoped and expected to be accessed from the main thread, which matches
 * where navigation calls originate. No explicit synchronization is applied to the key map.
 */
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class)
public class DefaultNavigationResultRegistry : NavigationResultRegistry {

    private val channels = mutableMapOf<NavigationResultRequest.Key<*>, Channel<Any>>()

    override fun <R : Any> register(key: NavigationResultRequest.Key<R>): Flow<R> {
        @Suppress("UNCHECKED_CAST")
        return channelFor(key).receiveAsFlow() as Flow<R>
    }

    override fun <R : Any> deliver(key: NavigationResultRequest.Key<R>, result: R) {
        channelFor(key).trySend(result)
    }

    private fun channelFor(key: NavigationResultRequest.Key<*>): Channel<Any> =
        channels.getOrPut(key) {
            Channel(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
        }
}
