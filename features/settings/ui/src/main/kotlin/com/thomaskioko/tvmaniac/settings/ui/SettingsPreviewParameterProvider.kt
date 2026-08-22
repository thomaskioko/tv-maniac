package com.thomaskioko.tvmaniac.settings.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthProviderOption
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.datastore.api.AutoBackupInterval
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.datastore.api.PosterCornerStyle
import com.thomaskioko.tvmaniac.datastore.api.PosterWidth
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.AutoBackupScheduleOption
import com.thomaskioko.tvmaniac.settings.presenter.AutoBackupSettings
import com.thomaskioko.tvmaniac.settings.presenter.BackupRestoreConfirmationDialog
import com.thomaskioko.tvmaniac.settings.presenter.BackupRestoreSummary
import com.thomaskioko.tvmaniac.settings.presenter.BackupSettings
import com.thomaskioko.tvmaniac.settings.presenter.DiscoverSectionToggle
import com.thomaskioko.tvmaniac.settings.presenter.PosterStyleLabels
import com.thomaskioko.tvmaniac.settings.presenter.PremiumState
import com.thomaskioko.tvmaniac.settings.presenter.SettingsCategoryGroup
import com.thomaskioko.tvmaniac.settings.presenter.SettingsCategoryItem
import com.thomaskioko.tvmaniac.settings.presenter.SettingsLabels
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private fun previewRootGroups(): ImmutableList<SettingsCategoryGroup> = buildList {
    add(
        SettingsCategoryGroup(
            label = "Account",
            items = persistentListOf(
                SettingsCategoryItem(SettingsPage.ACCOUNT, "Account", "Manage your connected account"),
            ),
        ),
    )
    add(
        SettingsCategoryGroup(
            label = "General",
            items = persistentListOf(
                SettingsCategoryItem(SettingsPage.APPEARANCE, "Appearance", "Theme and image quality"),
                SettingsCategoryItem(SettingsPage.LAYOUT, "Layout", "Posters, sections, and display"),
                SettingsCategoryItem(SettingsPage.BEHAVIOR, "Behavior", "Sync, specials, and trailers"),
                SettingsCategoryItem(SettingsPage.NOTIFICATIONS, "Notifications", "Episode release alerts"),
                SettingsCategoryItem(SettingsPage.PRIVACY, "Privacy", "Crash reporting and privacy policy"),
            ),
        ),
    )
    add(
        SettingsCategoryGroup(
            label = "About",
            items = persistentListOf(
                SettingsCategoryItem(SettingsPage.INFO, "Info", "App version and source code"),
                SettingsCategoryItem(SettingsPage.LICENSES, "Licenses & Attribution", "Data sources and acknowledgements"),
            ),
        ),
    )
}.toImmutableList()

private val previewLabels = SettingsLabels(
    back = "Back",
    themeTitle = "App Theme",
    themeSubtitle = "Choose how TvManiac looks to you",
    imageQualityTitle = "Image Quality",
    imageQualityDescription = "Balanced quality and data usage",
    imageQualityAuto = "Auto",
    imageQualityHigh = "High",
    imageQualityMedium = "Medium",
    imageQualityLow = "Low",
    syncTitle = "Sync & Update",
    syncDescription = "Auto-sync and update content",
    lastSync = null,
    includeSpecialsTitle = "Special Seasons",
    includeSpecialsDescription = "Display Specials and bonus seasons",
    quickRateTitle = "Quick Rate",
    quickRateDescription = "Show rating prompt after marking an episode as watched",
    multiplePlaysTitle = "Multiple plays",
    multiplePlaysDescription = "Let a show be marked watched more than once, and count the rewatches",
    youtubeTitle = "Trailers",
    youtubeDescription = "Open Trailers in Youtube App",
    episodeNotificationsTitle = "Episode Notifications",
    episodeNotificationsDescription = "Get notified when new episodes air",
    crashReportingTitle = "Crash Reporting",
    crashReportingDescription = "Send anonymous crash reports to help improve the app",
    hapticFeedbackTitle = "Haptic feedback",
    hapticFeedbackDescription = "Feel subtle vibrations during interactions",
    seasonOrderTitle = "Season Order",
    seasonOrderDescription = "Order the latest season first",
    blurUnwatchedTitle = "Hide Spoilers",
    blurUnwatchedDescription = "Hide spoilers for unwatched episodes",
    discoverSectionsTitle = "Discover Sections",
    discoverSectionsDescription = "Choose which sections appear on the Discover tab",
    fontSizeTitle = "Font size",
    fontSizeDescription = "Scale text across the app",
    fontSizePreview = "The quick brown fox jumps over the lazy dog",
    fontSizeReset = "Reset",
    posterStyle = PosterStyleLabels(
        title = "Poster style",
        subtitle = "Choose poster size and corner style",
        livePreview = "Live preview",
        reset = "Reset",
        postersLabel = "Posters",
        landscapeLabel = "Landscape",
        cornerLabel = "Corner style",
        widthCompact = "Compact",
        widthStandard = "Standard",
        widthComfortable = "Comfortable",
        widthLarge = "Large",
        cornerSharp = "Sharp",
        cornerClassic = "Classic",
        cornerRounded = "Rounded",
        cornerPill = "Pill",
    ),
    privacyPolicy = "Privacy Policy",
    appName = "TvManiac",
    version = "Version 1.0.0",
    aboutDescription = "TvManiac is a beautifully crafted Kotlin Multiplatform app for discovering and tracking " +
        "your favorite TV shows. Browse trending, popular, and top-rated shows, manage your watchlist, " +
        "track episodes you've watched, and sync everything with your Trakt account across Android and iOS.",
    sourceCode = "Source Code",
    github = "GitHub",
    apiDisclaimer = "This product uses the TMDB and Trakt API but is not endorsed or certified by either.",
    licensesApp = "App",
    licensesData = "Data & Services",
    tmdbTitle = "The Movie Database (TMDB)",
    tmdbBody = "TvManiac uses the TMDB API for show metadata, artwork, trailers, and cast. This product uses " +
        "the TMDB API but is not endorsed or certified by TMDB.",
    traktBody = "TvManiac uses Trakt to sync your watch history, watchlist, and episode progress across devices.",
    traktTitle = "Trakt",
    traktDescription = "Sync your watchlist, watch progress, continue watching, and personal lists with Trakt.",
    traktAuthentication = "Connect & Sync Your Content",
    connectTitle = "Connect",
    accountSyncDescription = "Save your progress, discover new titles, and sync your content across all devices.",
    traktConnected = "Connected as John Doe",
    traktConnectedDescription = "Your watch history, watchlist, and episode progress sync with Trakt.",
    logout = "Logout",
    login = "Login",
    switchConfirm = "Switch",
    switchCancel = "Cancel",
    switching = "Switching…",
)

private val loggedOutAccountLabels = previewLabels.copy(
    traktConnected = "Connect to Trakt",
    traktConnectedDescription = "Sign in with Trakt to sync your watch history, watchlist, and episode progress " +
        "across your devices.",
)

internal val defaultState = SettingsState(
    theme = ThemeModel.DARK,
    imageQuality = ImageQuality.HIGH,
    currentPageTitle = "Settings",
    rootGroups = previewRootGroups(),
    labels = previewLabels,
    showLogoutConfirmation = false,
    isAuthenticated = false,
    isLoading = false,
    openTrailersInYoutube = false,
    includeSpecials = false,
    versionName = "1.0.0",
)

internal val previewDiscoverSectionToggles: ImmutableList<DiscoverSectionToggle> = persistentListOf(
    DiscoverSectionToggle(DiscoverSection.START_WATCHING, "Start Watching", visible = true),
    DiscoverSectionToggle(DiscoverSection.TRENDING_TODAY, "Trending Today", visible = true),
    DiscoverSectionToggle(DiscoverSection.UPCOMING, "Upcoming", visible = false),
    DiscoverSectionToggle(DiscoverSection.POPULAR, "Popular", visible = true),
    DiscoverSectionToggle(DiscoverSection.TOP_RATED, "Top Rated", visible = true),
)

internal val loggedInState = SettingsState(
    theme = ThemeModel.DARK,
    imageQuality = ImageQuality.MEDIUM,
    currentPageTitle = "Settings",
    rootGroups = previewRootGroups(),
    labels = previewLabels,
    username = "John Doe",
    showLogoutConfirmation = false,
    isAuthenticated = true,
    isLoading = false,
    openTrailersInYoutube = true,
    includeSpecials = true,
    versionName = "1.0.0",
    discoverSectionToggles = previewDiscoverSectionToggles,
)

internal val appearanceState = loggedInState.copy(currentPage = SettingsPage.APPEARANCE, currentPageTitle = "Appearance")
internal val layoutState = loggedInState.copy(currentPage = SettingsPage.LAYOUT, currentPageTitle = "Layout")
internal val fontSizeScaledLayoutState = layoutState.copy(fontSizePercent = 118)
internal val discoverSectionsState = loggedInState.copy(
    currentPage = SettingsPage.DISCOVER_SECTIONS,
    currentPageTitle = "Discover Sections",
)
internal val posterStyleState = loggedInState.copy(
    currentPage = SettingsPage.POSTER_STYLE,
    currentPageTitle = "Poster Style",
)
internal val posterStyleLockedState = posterStyleState.copy(
    premium = PremiumState(
        posterStyleLocked = true,
        badgeText = "Premium",
        themesLockedTitle = "Poster styles are a Premium feature",
        themesLockedMessage = "Upgrade to Premium to customize poster size and shape.",
        upgradeText = "Upgrade to Premium",
        lockedContentDescription = "Locked",
    ),
)
internal val posterStyleMixedState = posterStyleState.copy(
    posterWidth = PosterWidth.LARGE,
    landscapeWidth = PosterWidth.COMPACT,
    posterCornerStyle = PosterCornerStyle.ROUNDED,
)
internal val appearanceLockedState = appearanceState.copy(
    premium = PremiumState(
        customThemesLocked = true,
        badgeText = "Premium",
        themesLockedTitle = "Custom themes are a Premium feature",
        themesLockedMessage = "Upgrade to Premium to use custom themes.",
        upgradeText = "Upgrade to Premium",
        lockedContentDescription = "Locked",
    ),
)
internal val behaviorState = loggedInState.copy(currentPage = SettingsPage.BEHAVIOR, currentPageTitle = "Behavior")
internal val behaviorSimklFreeTierState = behaviorState.copy(
    multiplePlaysSyncNotice = "Simkl does not store rewatches without a paid plan, so this count stays on this device.",
)
internal val behaviorLockedState = behaviorState.copy(
    premium = PremiumState(
        quickRateLocked = true,
        badgeText = "Premium",
        lockedContentDescription = "Locked",
    ),
)
internal val notificationsState = loggedInState.copy(currentPage = SettingsPage.NOTIFICATIONS, currentPageTitle = "Notifications")
internal val notificationsLockedState = notificationsState.copy(
    premium = PremiumState(
        episodeNotificationsLocked = true,
        badgeText = "Premium",
        lockedContentDescription = "Locked",
    ),
)
internal val privacyState = loggedInState.copy(currentPage = SettingsPage.PRIVACY, currentPageTitle = "Privacy")
private val autoBackupSettings = AutoBackupSettings(
    title = "Automatic backup",
    description = "Save your shows, watch history, ratings and settings to a file on a schedule",
    scheduleTitle = "How often",
    scheduleLabel = "Every week",
    scheduleOptions = persistentListOf(
        AutoBackupScheduleOption(interval = AutoBackupInterval.DAILY, label = "Every day", selected = false),
        AutoBackupScheduleOption(interval = AutoBackupInterval.WEEKLY, label = "Every week", selected = true),
        AutoBackupScheduleOption(interval = AutoBackupInterval.FORTNIGHTLY, label = "Every two weeks", selected = false),
        AutoBackupScheduleOption(interval = AutoBackupInterval.MONTHLY, label = "Every month", selected = false),
    ),
    locationTitle = "Backup location",
    locationLabel = "Choose where to save backups",
    fileNameTitle = "File name",
    fileNameMessage = "Backups are saved under this name",
    fileName = "tvmaniac-backup.json",
    fileNameSaveLabel = "Save",
    fileNameCancelLabel = "Cancel",
    lastRunLabel = "No backup saved yet",
    backupNowTitle = "Back up now",
    backupNowDescription = "Save a backup straight away, without waiting for the schedule",
)
internal val backupState = loggedInState.copy(
    currentPage = SettingsPage.BACKUP,
    currentPageTitle = "Backup",
    backup = BackupSettings(
        exportTitle = "Export backup",
        exportDescription = "Save your tracking data and preferences to a file",
        importTitle = "Restore a backup",
        importDescription = "Replace what is on this device with a backup file",
        autoBackup = autoBackupSettings,
    ),
)
internal val backupAutoBackupOnState = backupState.copy(
    backup = backupState.backup.copy(
        autoBackup = autoBackupSettings.copy(
            enabled = true,
            hasLocation = true,
            locationLabel = "Download",
            lastRunLabel = "Last backup 12 August 2026",
        ),
    ),
)
internal val backupAutoBackupNeverRunState = backupState.copy(
    backup = backupState.backup.copy(
        autoBackup = autoBackupSettings.copy(enabled = true),
    ),
)
internal val backupAutoBackupFailedState = backupState.copy(
    backup = backupState.backup.copy(
        autoBackup = autoBackupSettings.copy(
            enabled = true,
            hasLocation = true,
            locationLabel = "Download",
            lastRunLabel = "Last backup 12 August 2026",
            failureWarning = "The last automatic backup failed. Check the location is still " +
                "available, then back up now.",
        ),
    ),
)
internal val backupAutoBackupRunningState = backupAutoBackupOnState.copy(
    backup = backupAutoBackupOnState.backup.copy(
        autoBackup = backupAutoBackupOnState.backup.autoBackup.copy(isBackingUp = true),
    ),
)
internal val backupLockedState = backupState.copy(
    premium = PremiumState(
        backupLocked = true,
        badgeText = "Premium",
        backupLockedTitle = "Backup is a Premium feature",
        backupLockedMessage = "Upgrade to Premium to back up your data to a file.",
        upgradeText = "Upgrade to Premium",
        lockedContentDescription = "Locked",
    ),
)
internal val backupExportingState = backupState.copy(
    backup = backupState.backup.copy(isExporting = true),
)
internal val backupImportingState = backupState.copy(
    backup = backupState.backup.copy(isImporting = true),
)
internal val backupRestoreConfirmState = backupState.copy(
    backup = backupState.backup.copy(
        confirm = BackupRestoreConfirmationDialog.Local(
            title = "Restore this backup?",
            message = "This replaces the shows and watch history on this device. " +
                "A copy of your current data is saved first.",
            confirmLabel = "Restore",
            cancelLabel = "Cancel",
        ),
    ),
)
internal val backupRestoreConfirmConnectedState = backupState.copy(
    activeProvider = SyncProviderSource.TRAKT,
    backup = backupState.backup.copy(
        confirm = BackupRestoreConfirmationDialog.Connected(
            title = "Restore this backup?",
            message = "This replaces the shows and watch history on this device, and a copy of " +
                "your current data is saved first. You are signed in to Trakt, so shows you do " +
                "not add to it are removed at the next sync.",
            cancelLabel = "Cancel",
            accountLabel = "Restore and sync with Trakt",
            deviceLabel = "Restore locally",
        ),
    ),
)
internal val backupRestoreSummaryState = backupState.copy(
    backup = backupState.backup.copy(
        summary = BackupRestoreSummary(
            title = "Restore finished",
            showsRestored = "42 shows restored",
            episodesRestored = "612 episodes restored",
        ),
    ),
)
internal val backupRestoreSummaryWithSkipsState = backupState.copy(
    backup = backupState.backup.copy(
        summary = BackupRestoreSummary(
            title = "Restore finished",
            showsRestored = "40 shows restored",
            episodesRestored = "598 episodes restored",
            showsSkipped = "2 shows skipped",
            skippedShows = persistentListOf("Breaking Bad", "The Wire"),
            rewatchNotice = "Repeat viewings were not included in this backup",
        ),
    ),
)
internal val infoState = loggedInState.copy(currentPage = SettingsPage.INFO, currentPageTitle = "Info")
internal val licensesState = loggedInState.copy(currentPage = SettingsPage.LICENSES, currentPageTitle = "Licenses & Attribution")
internal val accountState = loggedInState.copy(
    currentPage = SettingsPage.ACCOUNT,
    currentPageTitle = "Account",
    activeProvider = SyncProviderSource.TRAKT,
    accountConnectedDescription = "Your watch history, watchlist, and episode progress sync with Trakt.",
)
internal val accountLoggedOutState = defaultState.copy(
    currentPage = SettingsPage.ACCOUNT,
    currentPageTitle = "Account",
    labels = loggedOutAccountLabels,
    authProviders = persistentListOf(
        AuthProviderOption(SyncProviderSource.TRAKT, "Continue with Trakt"),
        AuthProviderOption(SyncProviderSource.SIMKL, "Continue with Simkl"),
    ),
)

internal val accountSwitchState = accountState.copy(
    switchTargetProvider = SyncProviderSource.SIMKL,
    switchActionLabel = "Switch to Simkl",
)

internal val accountSwitchDialogState = accountSwitchState.copy(
    showSwitchConfirmation = true,
    pendingSwitchProvider = SyncProviderSource.SIMKL,
    switchUnsavedCount = 3,
    switchDialogTitle = "Switch to Simkl?",
    switchDialogMessage = "You have 3 unsynced items. Switching providers may cause data loss.",
)

internal val accountSwitchingState = accountSwitchState.copy(
    isSwitching = true,
)

internal val accountLoggingOutState = accountState.copy(
    isProcessingAuth = true,
)

internal val loadingState = defaultState.copy(isLoading = true)

internal class SettingsPreviewParameterProvider : PreviewParameterProvider<SettingsState> {
    override val values: Sequence<SettingsState>
        get() {
            return sequenceOf(
                loadingState,
                defaultState,
                loggedInState,
                appearanceState,
                layoutState,
                discoverSectionsState,
                posterStyleState,
                behaviorState,
            )
        }
}

internal class BackupPreviewParameterProvider : PreviewParameterProvider<SettingsState> {
    override val values: Sequence<SettingsState>
        get() {
            return sequenceOf(
                backupState,
                backupLockedState,
                backupExportingState,
                backupImportingState,
                backupRestoreConfirmState,
                backupRestoreConfirmConnectedState,
                backupRestoreSummaryState,
                backupRestoreSummaryWithSkipsState,
                backupAutoBackupOnState,
                backupAutoBackupNeverRunState,
                backupAutoBackupFailedState,
                backupAutoBackupRunningState,
            )
        }
}
