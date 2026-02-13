package com.thomaskioko.tvmaniac.debug.presenter

public data class DebugState(
    val isLoading: Boolean = false,
) {
    public companion object {
        public val DEFAULT_STATE: DebugState = DebugState()
    }
}
