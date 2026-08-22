package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.accountmanager.api.AuthProviderOption
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.datastore.api.AutoBackupInterval
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.datastore.api.PosterCornerStyle
import com.thomaskioko.tvmaniac.datastore.api.PosterWidth
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class SettingsState(
    val isAuthenticated: Boolean,
    val activeProvider: SyncProviderSource? = null,
    val activeProviderName: String? = null,
    val authProviders: ImmutableList<AuthProviderOption> = persistentListOf(),
    val accountConnectedDescription: String? = null,
    val switchTargetProvider: SyncProviderSource? = null,
    val switchActionLabel: String? = null,
    val isSwitching: Boolean = false,
    val showSwitchConfirmation: Boolean = false,
    val switchUnsavedCount: Int = 0,
    val pendingSwitchProvider: SyncProviderSource? = null,
    val switchDialogTitle: String? = null,
    val switchDialogMessage: String? = null,
    val theme: ThemeModel,
    val imageQuality: ImageQuality,
    val currentPage: SettingsPage = SettingsPage.ROOT,
    val currentPageTitle: String = "",
    val rootGroups: ImmutableList<SettingsCategoryGroup> = persistentListOf(),
    val labels: SettingsLabels = SettingsLabels(),
    val premium: PremiumState = PremiumState(),
    val backup: BackupSettings = BackupSettings(),
    val username: String? = null,
    val showLogoutConfirmation: Boolean,
    val message: UiMessage? = null,
    val openTrailersInYoutube: Boolean = false,
    val includeSpecials: Boolean = false,
    val quickRateEnabled: Boolean = false,
    val multiplePlaysEnabled: Boolean = true,
    val multiplePlaysSyncNotice: String? = null,
    val backgroundSyncEnabled: Boolean = true,
    val lastSyncDate: String? = null,
    val showLastSyncDate: Boolean = false,
    val versionName: String,
    val episodeNotificationsEnabled: Boolean = false,
    val crashReportingEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val newestSeasonFirst: Boolean = false,
    val blurImage: Boolean = false,
    val discoverSectionToggles: ImmutableList<DiscoverSectionToggle> = persistentListOf(),
    val fontSizePercent: Int = 100,
    val posterWidth: PosterWidth = PosterWidth.STANDARD,
    val landscapeWidth: PosterWidth = PosterWidth.STANDARD,
    val posterCornerStyle: PosterCornerStyle = PosterCornerStyle.SHARP,
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val isProcessingAuth: Boolean = false,
    val hiddenTapCount: Int = 0,
    val isDebugMenuEnabled: Boolean = false,
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

public data class DiscoverSectionToggle(
    val section: DiscoverSection,
    val label: String,
    val visible: Boolean,
)

public data class BackupSettings(
    val isExporting: Boolean = false,
    val awaitingDestination: Boolean = false,
    val exportTitle: String = "",
    val exportDescription: String = "",
    val importTitle: String = "",
    val importDescription: String = "",
    val isImporting: Boolean = false,
    val awaitingSource: Boolean = false,
    val syncWithConnectedAccount: Boolean = false,
    val choosingAutoBackupLocation: Boolean = false,
    val confirm: BackupRestoreConfirmationDialog? = null,
    val summary: BackupRestoreSummary? = null,
    val autoBackup: AutoBackupSettings = AutoBackupSettings(),
)

public data class AutoBackupSettings(
    val title: String = "",
    val description: String = "",
    val enabled: Boolean = false,
    val scheduleTitle: String = "",
    val scheduleLabel: String = "",
    val scheduleOptions: ImmutableList<AutoBackupScheduleOption> = persistentListOf(),
    val locationTitle: String = "",
    val locationLabel: String = "",
    val hasLocation: Boolean = false,
    val fileNameTitle: String = "",
    val fileNameMessage: String = "",
    val fileName: String = "",
    val fileNameSaveLabel: String = "",
    val fileNameCancelLabel: String = "",
    val lastRunLabel: String = "",
    val failureWarning: String? = null,
    val backupNowTitle: String = "",
    val backupNowDescription: String = "",
    val isBackingUp: Boolean = false,
)

public data class AutoBackupScheduleOption(
    val interval: AutoBackupInterval,
    val label: String,
    val selected: Boolean,
)

public sealed interface BackupRestoreConfirmationDialog {
    public val title: String
    public val message: String
    public val cancelLabel: String

    public data class Local(
        override val title: String,
        override val message: String,
        override val cancelLabel: String,
        val confirmLabel: String,
    ) : BackupRestoreConfirmationDialog

    public data class Connected(
        override val title: String,
        override val message: String,
        override val cancelLabel: String,
        val accountLabel: String,
        val deviceLabel: String,
    ) : BackupRestoreConfirmationDialog
}

public data class BackupRestoreSummary(
    val title: String,
    val showsRestored: String,
    val episodesRestored: String,
    val showsSkipped: String? = null,
    val skippedShows: ImmutableList<String> = persistentListOf(),
    val rewatchNotice: String? = null,
)

public data class PremiumState(
    val backupLocked: Boolean = false,
    val customThemesLocked: Boolean = false,
    val posterStyleLocked: Boolean = false,
    val episodeNotificationsLocked: Boolean = false,
    val quickRateLocked: Boolean = false,
    val badgeText: String = "",
    val backupLockedTitle: String = "",
    val backupLockedMessage: String = "",
    val themesLockedTitle: String = "",
    val themesLockedMessage: String = "",
    val upgradeText: String = "",
    val lockedContentDescription: String = "",
)
