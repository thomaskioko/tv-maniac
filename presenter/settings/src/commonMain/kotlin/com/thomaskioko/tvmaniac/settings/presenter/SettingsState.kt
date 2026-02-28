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
    val episodeNotificationsEnabled: Boolean = false,
    val crashReportingEnabled: Boolean = true,
    val isUpdating: Boolean = false,
    val isDebugBuild: Boolean = false,
    val githubUrl: String = GITHUB_URL,
    val privacyPolicyUrl: String = PRIVACY_POLICY_URL,
) {
    public companion object {
        private const val GITHUB_URL = "https://github.com/c0de-wizard/tv-maniac"
        private const val PRIVACY_POLICY_URL = "https://github.com/c0de-wizard/tv-maniac"

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
            episodeNotificationsEnabled = false,
            crashReportingEnabled = true,
            isDebugBuild = false,
        )
    }
}
