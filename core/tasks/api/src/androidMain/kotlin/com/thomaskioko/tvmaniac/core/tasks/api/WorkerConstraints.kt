package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * Conditions that must be satisfied before a [BackgroundWorker] can execute.
 *
 * @property networkType The network condition required for execution.
 */
public data class WorkerConstraints(
    public val networkType: NetworkRequirement = NetworkRequirement.NOT_REQUIRED,
)

/**
 * Network condition required for a [BackgroundWorker] to execute.
 */
public enum class NetworkRequirement {
    /** No network needed. */
    NOT_REQUIRED,

    /** Any active network connection (Wi-Fi, cellular, etc.). */
    CONNECTED,

    /** Unmetered network only (typically Wi-Fi). */
    UNMETERED,
}
