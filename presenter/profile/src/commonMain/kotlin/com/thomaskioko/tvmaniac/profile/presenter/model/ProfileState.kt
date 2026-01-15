package com.thomaskioko.tvmaniac.profile.presenter.model

import com.thomaskioko.tvmaniac.core.view.UiMessage

public data class ProfileState(
    val isLoading: Boolean,
    val isAuthenticating: Boolean = false,
    val userProfile: ProfileInfo?,
    val errorMessage: UiMessage? = null,
    val authenticated: Boolean,
) {
    val showLoading: Boolean
        get() = userProfile == null && errorMessage == null && (isLoading || isAuthenticating || authenticated)

    public companion object {
        public val DEFAULT_STATE: ProfileState = ProfileState(
            isLoading = false,
            isAuthenticating = false,
            userProfile = null,
            errorMessage = null,
            authenticated = false,
        )
    }
}
