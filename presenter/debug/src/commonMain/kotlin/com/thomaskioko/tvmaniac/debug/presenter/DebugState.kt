package com.thomaskioko.tvmaniac.debug.presenter

import com.thomaskioko.tvmaniac.core.view.UiMessage

public data class DebugState(
    val isLoading: Boolean = false,
    val isSchedulingDebugNotification: Boolean = false,
    val isSyncingLibrary: Boolean = false,
    val isSyncingUpNext: Boolean = false,
    val lastLibrarySyncDate: String? = null,
    val lastUpNextSyncDate: String? = null,
    val message: UiMessage? = null,
) {
    public companion object {
        public val DEFAULT_STATE: DebugState = DebugState()
    }
}
