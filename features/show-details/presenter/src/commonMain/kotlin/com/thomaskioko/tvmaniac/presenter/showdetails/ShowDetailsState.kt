package com.thomaskioko.tvmaniac.presenter.showdetails

import com.thomaskioko.tvmaniac.core.view.UiMessage

public data class ShowDetailsState(
    val isRefreshing: Boolean = false,
    val message: UiMessage? = null,
) {
    public companion object {
        public val Empty: ShowDetailsState = ShowDetailsState()
    }
}
