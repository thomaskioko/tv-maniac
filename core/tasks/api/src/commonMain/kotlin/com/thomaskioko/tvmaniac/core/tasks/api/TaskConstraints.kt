package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * Conditions that must be met before the OS triggers a background task.
 *
 * @property requiresNetwork When `true`, the task will only run when any network
 *   connection (cellular or Wi-Fi) is available.
 * @property requiresUnmeteredNetwork When `true`, the task will only run on an
 *   unmetered (typically Wi-Fi) connection. Implies [requiresNetwork].
 */
public data class TaskConstraints(
    val requiresNetwork: Boolean = false,
    val requiresUnmeteredNetwork: Boolean = false,
)
