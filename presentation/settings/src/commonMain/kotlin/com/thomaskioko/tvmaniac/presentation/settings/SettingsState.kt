package com.thomaskioko.tvmaniac.presentation.settings

import com.thomaskioko.tvmaniac.datastore.api.Theme

data class SettingsState(
    val userInfo: UserInfo?,
    val theme: Theme,
    val showTraktDialog: Boolean,
    val showthemePopup: Boolean,
    val errorMessage: String?,
    val showLogoutDialog: Boolean,
    val isLoading: Boolean,
) {
    companion object {
        val DEFAULT_STATE = SettingsState(
            userInfo = null,
            theme = Theme.SYSTEM,
            showTraktDialog = false,
            showthemePopup = false,
            isLoading = false,
            errorMessage = null,
            showLogoutDialog = true,
        )
    }
}

data class UserInfo(
    val slug: String,
    val userName: String?,
    val fullName: String?,
    val userPicUrl: String?,
)
