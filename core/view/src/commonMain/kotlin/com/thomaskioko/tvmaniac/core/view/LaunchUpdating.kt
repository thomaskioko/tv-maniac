package com.thomaskioko.tvmaniac.core.view

import com.thomaskioko.tvmaniac.core.logger.Logger
import kotlinx.collections.immutable.PersistentSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Flags [id] in [updatingIds] while [statusFlow] runs, driving a per-item progress indicator.
 * Removes [id] once collection finishes, on success or failure, and routes errors through
 * [collectStatus]. Ignores re-dispatch while [id] is already updating.
 */
public fun CoroutineScope.launchUpdating(
    id: Long,
    updatingIds: MutableStateFlow<PersistentSet<Long>>,
    logger: Logger? = null,
    uiMessageManager: UiMessageManager? = null,
    sourceId: String? = null,
    errorToStringMapper: ErrorToStringMapper? = null,
    statusFlow: () -> Flow<InvokeStatus>,
) {
    if (id in updatingIds.value) return
    updatingIds.update { it.adding(id) }
    launch {
        try {
            statusFlow().collectStatus(
                logger = logger,
                uiMessageManager = uiMessageManager,
                sourceId = sourceId,
                errorToStringMapper = errorToStringMapper,
            )
        } finally {
            updatingIds.update { it.removing(id) }
        }
    }
}
