package com.thomaskioko.tvmaniac.presentation.settings

import com.thomaskioko.tvmaniac.datastore.api.AppTheme

data class SettingsState(
    val userInfo: UserInfo?,
    val appTheme: AppTheme,
    val showTraktDialog: Boolean,
    val showthemePopup: Boolean,
    val errorMessage: String?,
    val showLogoutDialog: Boolean,
    val isLoading: Boolean,
    val openTrailersInYoutube: Boolean = false, //TODO:: Add implementation
) {
    companion object {
        val DEFAULT_STATE = SettingsState(
            userInfo = null,
            appTheme = AppTheme.SYSTEM_THEME,
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
