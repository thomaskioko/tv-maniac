package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality

data class SettingsState(
    val isAuthenticated: Boolean,
    val appTheme: AppTheme,
    val imageQuality: ImageQuality,
    val showTraktDialog: Boolean,
    val showthemePopup: Boolean,
    val showImageQualityDialog: Boolean,
    val errorMessage: String?,
    val showLogoutDialog: Boolean,
    val openTrailersInYoutube: Boolean = false, // TODO:: Add implementation
) {
    companion object {
        val DEFAULT_STATE = SettingsState(
            isAuthenticated = false,
            appTheme = AppTheme.SYSTEM_THEME,
            imageQuality = ImageQuality.MEDIUM,
            showTraktDialog = false,
            showthemePopup = false,
            showImageQualityDialog = false,
            errorMessage = null,
            showLogoutDialog = true,
        )
    }
}
