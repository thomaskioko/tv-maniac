package com.thomaskioko.tvmaniac.navigation

import kotlinx.coroutines.flow.Flow

/**
 * Brokers [NavigationResultRequest] deliveries between destinations at activity scope.
 *
 * Presenters that need to receive a result inject [NavigationResultRegistry] and call
 * [registerForNavigationResult]. Presenters that need to deliver a result inject the same registry
 * and call [deliverNavigationResult]. The registry keeps one pending result for each key and
 * delivers it on the next collection.
 *
 * Results live in memory only. They survive Decompose recompositions but do not persist across
 * process death in the current implementation. Treat delivered results as at-most-once hints and
 * reconcile authoritative state through repositories when that guarantee matters.
 */
public interface NavigationResultRegistry {

    /**
     * Returns a [Flow] that emits the next result delivered for [key]. Each delivered result is
     * emitted exactly once to the first collector that observes it; later collectors receive only
     * new deliveries.
     *
     * @param R result type associated with [key].
     * @param key identifier matched against [deliver] calls.
     * @return cold flow that emits each delivered result exactly once.
     */
    public fun <R : Any> register(key: NavigationResultRequest.Key<R>): Flow<R>

    /**
     * Delivers [result] to any present or future collector registered for [key]. If a collector
     * is already observing the flow, it receives the value immediately; otherwise the value is
     * buffered until one starts collecting.
     *
     * @param R result type associated with [key].
     * @param key identifier registered by the source destination.
     * @param result value to deliver to the source.
     */
    public fun <R : Any> deliver(key: NavigationResultRequest.Key<R>, result: R)
}
