package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthProviderOption
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class SettingsState(
    val isAuthenticated: Boolean,
    val activeProvider: AccountProvider? = null,
    val authProviders: ImmutableList<AuthProviderOption> = persistentListOf(),
    val accountConnectedDescription: String? = null,
    val switchTargetProvider: AccountProvider? = null,
    val switchActionLabel: String? = null,
    val isSwitching: Boolean = false,
    val showSwitchConfirmation: Boolean = false,
    val switchUnsavedCount: Int = 0,
    val pendingSwitchProvider: AccountProvider? = null,
    val switchDialogTitle: String? = null,
    val switchDialogMessage: String? = null,
    val theme: ThemeModel,
    val imageQuality: ImageQuality,
    val currentPage: SettingsPage = SettingsPage.ROOT,
    val currentPageTitle: String = "",
    val rootGroups: ImmutableList<SettingsCategoryGroup> = persistentListOf(),
    val labels: SettingsLabels = SettingsLabels(),
    val username: String? = null,
    val showLogoutConfirmation: Boolean,
    val message: UiMessage? = null,
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
    val isProcessingAuth: Boolean = false,
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
            showLogoutConfirmation = false,
            message = null,
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
