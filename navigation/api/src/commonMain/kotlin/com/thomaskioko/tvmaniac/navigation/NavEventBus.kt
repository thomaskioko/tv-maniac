package com.thomaskioko.tvmaniac.navigation

import kotlinx.coroutines.flow.SharedFlow

/**
 * App-wide bus for broadcasting [NavEvent]s across presenter and navigator boundaries without
 * creating direct dependencies between them. Producers call [emit]; consumers collect [events].
 *
 * Backed by a replay-less [SharedFlow], so only listeners active at the moment of emission
 * observe the event. There is a single bus instance bound at activity scope.
 */
public interface NavEventBus {
    /** Stream of navigation events. */
    public val events: SharedFlow<NavEvent>

    /** Publishes [event] to all current collectors of [events]. */
    public fun emit(event: NavEvent)
}
