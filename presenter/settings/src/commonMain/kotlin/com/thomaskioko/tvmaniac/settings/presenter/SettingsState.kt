package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.datastore.api.ImageQuality

public data class SettingsState(
    val isAuthenticated: Boolean,
    val theme: ThemeModel,
    val imageQuality: ImageQuality,
    val showTraktDialog: Boolean,
    val showthemePopup: Boolean,
    val showAboutDialog: Boolean,
    val errorMessage: String?,
    val showLogoutDialog: Boolean,
    val openTrailersInYoutube: Boolean = false,
    val includeSpecials: Boolean = false,
    val backgroundSyncEnabled: Boolean = true,
    val lastSyncDate: String? = null,
    val showLastSyncDate: Boolean = false,
    val versionName: String,
    val isDebugBuild: Boolean = false,
) {
    public companion object {
        public val DEFAULT_STATE: SettingsState = SettingsState(
            isAuthenticated = false,
            theme = ThemeModel.SYSTEM,
            imageQuality = ImageQuality.AUTO,
            showTraktDialog = false,
            showthemePopup = false,
            showAboutDialog = false,
            errorMessage = null,
            showLogoutDialog = false,
            includeSpecials = false,
            backgroundSyncEnabled = true,
            lastSyncDate = null,
            showLastSyncDate = false,
            versionName = "0.0.0",
        )
    }
}
