package com.thomaskioko.tvmaniac.profile.presenter.model

import com.thomaskioko.tvmaniac.core.view.UiMessage

public data class ProfileState(
    val isLoading: Boolean,
    val isRefreshing: Boolean = false,
    val userProfile: ProfileInfo?,
    val errorMessage: UiMessage? = null,
    val authenticated: Boolean,
) {
    public companion object {
        public val DEFAULT_STATE: ProfileState = ProfileState(
            isLoading = false,
            isRefreshing = false,
            userProfile = null,
            errorMessage = null,
            authenticated = false,
        )
    }
}
