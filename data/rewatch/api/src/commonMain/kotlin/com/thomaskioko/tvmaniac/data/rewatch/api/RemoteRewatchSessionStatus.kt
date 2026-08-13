package com.thomaskioko.tvmaniac.data.rewatch.api

/**
 * The state a provider reports for a rewatch session.
 *
 * An unrecognised value maps to [ACTIVE], since treating an unknown state as finished would close
 * a session the user is still working through.
 */
public enum class RemoteRewatchSessionStatus {
    ACTIVE,
    CLOSED,
    COMPLETED,
    ;

    public companion object {
        public fun fromRaw(raw: String?): RemoteRewatchSessionStatus = when (raw) {
            "closed" -> CLOSED
            "completed" -> COMPLETED
            else -> ACTIVE
        }
    }
}
