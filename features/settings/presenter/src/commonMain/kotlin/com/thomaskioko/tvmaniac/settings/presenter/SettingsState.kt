package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class SettingsState(
    val isAuthenticated: Boolean,
    val theme: ThemeModel,
    val imageQuality: ImageQuality,
    val currentPage: SettingsPage = SettingsPage.ROOT,
    val currentPageTitle: String = "",
    val rootGroups: ImmutableList<SettingsCategoryGroup> = persistentListOf(),
    val labels: SettingsLabels = SettingsLabels(),
    val username: String? = null,
    val showTraktDialog: Boolean,
    val message: UiMessage? = null,
    val showLogoutDialog: Boolean,
    val openTrailersInYoutube: Boolean = false,
    val includeSpecials: Boolean = false,
    val backgroundSyncEnabled: Boolean = true,
    val lastSyncDate: String? = null,
    val showLastSyncDate: Boolean = false,
    val versionName: String,
    val episodeNotificationsEnabled: Boolean = false,
    val crashReportingEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val isProcessingTraktAuth: Boolean = false,
    val hiddenTapCount: Int = 0,
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
            message = null,
            showLogoutDialog = false,
            includeSpecials = false,
            backgroundSyncEnabled = true,
            lastSyncDate = null,
            showLastSyncDate = false,
            versionName = "0.0.0",
            episodeNotificationsEnabled = false,
            crashReportingEnabled = true,
        )
    }
}
