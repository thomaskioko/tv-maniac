package com.thomaskioko.tvmaniac.navigation

/**
 * Signal broadcast on [NavEventBus] when a navigation-adjacent side effect occurs that more than
 * one consumer may care about. Use this instead of wiring direct dependencies between presenters
 * or navigators.
 */
public sealed interface NavEvent {
    /**
     * Emitted after the user follows a show. Listeners (for example, the root presenter) use this
     * to trigger notification permission rationale or other one-off follow-ups.
     */
    public data object ShowFollowed : NavEvent
}
