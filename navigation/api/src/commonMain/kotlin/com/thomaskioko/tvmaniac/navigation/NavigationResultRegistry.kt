package com.thomaskioko.tvmaniac.navigation

import kotlinx.coroutines.flow.Flow

/**
 * Activity-scoped registry that brokers [NavigationResultRequest] deliveries between destinations.
 *
 * Presenters that need to receive a result inject [NavigationResultRegistry] and call
 * [registerForNavigationResult]. Presenters that need to deliver a result also inject the registry
 * and call [deliverNavigationResult]. The registry keeps a single pending result per key and
 * delivers it on the next collection.
 *
 * Results are kept in memory only. They survive Decompose recompositions but do not persist across
 * process death in the current implementation. Treat delivered results as at-most-once hints and
 * reconcile authoritative state through repositories when that guarantee matters.
 */
public interface NavigationResultRegistry {

    /**
     * Returns a [Flow] that emits the next result delivered for [key]. Each delivered result is
     * emitted exactly once to the first collector that observes it; subsequent collectors receive
     * only new deliveries.
     */
    public fun <R : Any> register(key: NavigationResultRequest.Key<R>): Flow<R>

    /**
     * Delivers [result] to any present or future collector registered for [key]. If a collector
     * is already observing the flow, it receives the value immediately; otherwise the value is
     * buffered until one starts collecting.
     */
    public fun <R : Any> deliver(key: NavigationResultRequest.Key<R>, result: R)
}
