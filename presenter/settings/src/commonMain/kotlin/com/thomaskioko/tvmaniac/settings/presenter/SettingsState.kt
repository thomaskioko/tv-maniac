package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.datastore.api.ImageQuality

data class SettingsState(
    val isAuthenticated: Boolean,
    val theme: ThemeModel,
    val imageQuality: ImageQuality,
    val showTraktDialog: Boolean,
    val showthemePopup: Boolean,
    val showAboutDialog: Boolean,
    val errorMessage: String?,
    val showLogoutDialog: Boolean,
    val openTrailersInYoutube: Boolean = false,
) {
    companion object {
        val DEFAULT_STATE = SettingsState(
            isAuthenticated = false,
            theme = ThemeModel.SYSTEM,
            imageQuality = ImageQuality.MEDIUM,
            showTraktDialog = false,
            showthemePopup = false,
            showAboutDialog = false,
            errorMessage = null,
            showLogoutDialog = false,
        )
    }
}
