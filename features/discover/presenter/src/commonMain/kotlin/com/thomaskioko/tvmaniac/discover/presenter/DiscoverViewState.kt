package com.thomaskioko.tvmaniac.discover.presenter

import com.thomaskioko.tvmaniac.core.view.UiMessage

public data class DiscoverViewState(
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val showError: Boolean = false,
    val message: UiMessage? = null,
) {
    public companion object {
        public val Empty: DiscoverViewState = DiscoverViewState()
    }
}
