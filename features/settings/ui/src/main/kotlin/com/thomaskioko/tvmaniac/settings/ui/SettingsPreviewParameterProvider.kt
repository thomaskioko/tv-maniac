package com.thomaskioko.tvmaniac.settings.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthProviderOption
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.DiscoverSectionToggle
import com.thomaskioko.tvmaniac.settings.presenter.SettingsCategoryGroup
import com.thomaskioko.tvmaniac.settings.presenter.SettingsCategoryItem
import com.thomaskioko.tvmaniac.settings.presenter.SettingsLabels
import com.thomaskioko.tvmaniac.settings.presenter.SettingsLocks
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
    discoverSectionsDescription = "Choose which sections appear on Discover",
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
internal val discoverSectionsState = loggedInState.copy(
    currentPage = SettingsPage.DISCOVER_SECTIONS,
    currentPageTitle = "Discover Sections",
)
internal val appearanceLockedState = appearanceState.copy(
    locks = SettingsLocks(
        customThemesLocked = true,
        badgeText = "Premium",
        themesLockedTitle = "Custom themes are a Premium feature",
        themesLockedMessage = "Upgrade to Premium to use custom themes.",
        upgradeText = "Upgrade to Premium",
        lockedContentDescription = "Locked",
    ),
)
internal val behaviorState = loggedInState.copy(currentPage = SettingsPage.BEHAVIOR, currentPageTitle = "Behavior")
internal val notificationsState = loggedInState.copy(currentPage = SettingsPage.NOTIFICATIONS, currentPageTitle = "Notifications")
internal val notificationsLockedState = notificationsState.copy(
    locks = SettingsLocks(
        episodeNotificationsLocked = true,
        badgeText = "Premium",
        lockedContentDescription = "Locked",
    ),
)
internal val privacyState = loggedInState.copy(currentPage = SettingsPage.PRIVACY, currentPageTitle = "Privacy")
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
                behaviorState,
            )
        }
}
