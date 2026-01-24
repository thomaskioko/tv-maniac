package com.thomaskioko.tvmaniac.profile.presenter.model

import com.thomaskioko.tvmaniac.core.view.UiMessage

public data class ProfileState(
    val isLoading: Boolean,
    val userProfile: ProfileInfo?,
    val errorMessage: UiMessage? = null,
    val authenticated: Boolean,
) {
    val showLoading: Boolean
        get() = userProfile == null && errorMessage == null && isLoading

    public companion object {
        public val DEFAULT_STATE: ProfileState = ProfileState(
            isLoading = true,
            userProfile = null,
            errorMessage = null,
            authenticated = false,
        )
    }
}
