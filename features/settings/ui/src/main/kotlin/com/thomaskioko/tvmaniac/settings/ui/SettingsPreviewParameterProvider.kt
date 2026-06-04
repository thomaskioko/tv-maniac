package com.thomaskioko.tvmaniac.settings.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.SettingsCategoryGroup
import com.thomaskioko.tvmaniac.settings.presenter.SettingsCategoryItem
import com.thomaskioko.tvmaniac.settings.presenter.SettingsLabels
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private fun previewRootGroups(authenticated: Boolean): ImmutableList<SettingsCategoryGroup> = buildList {
    if (authenticated) {
        add(
            SettingsCategoryGroup(
                label = "Account",
                items = persistentListOf(
                    SettingsCategoryItem(SettingsPage.TRAKT, "Trakt Account", "Manage your Trakt connection"),
                ),
            ),
        )
    }
    add(
        SettingsCategoryGroup(
            label = "General",
            items = persistentListOf(
                SettingsCategoryItem(SettingsPage.APPEARANCE, "Appearance", "Theme and image quality"),
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
    traktAuthentication = "Authentication",
    traktConnected = "Connected as John Doe",
    traktConnectedDescription = "Your watch history, watchlist, and episode progress sync with Trakt.",
    logout = "Logout",
    login = "Login",
)

private val loggedOutTraktLabels = previewLabels.copy(
    traktConnected = "Connect to Trakt",
    traktConnectedDescription = "You are about to be redirected to your browser and outside of TvManiac app, " +
        "where you will be taken to the Trakt website. From there, you will need to authorise TvManiac access " +
        "to your Trakt account in order to make use of the Trakt functionality around the app. After you " +
        "authorize, you will return to the app and you can continue with business as usual.",
)

internal val defaultState = SettingsState(
    theme = ThemeModel.DARK,
    imageQuality = ImageQuality.HIGH,
    currentPageTitle = "Settings",
    rootGroups = previewRootGroups(authenticated = false),
    labels = previewLabels,
    showTraktDialog = false,
    showLogoutDialog = false,
    isAuthenticated = false,
    isLoading = false,
    openTrailersInYoutube = false,
    includeSpecials = false,
    versionName = "1.0.0",
)

internal val loggedInState = SettingsState(
    theme = ThemeModel.DARK,
    imageQuality = ImageQuality.MEDIUM,
    currentPageTitle = "Settings",
    rootGroups = previewRootGroups(authenticated = true),
    labels = previewLabels,
    username = "John Doe",
    showTraktDialog = false,
    showLogoutDialog = false,
    isAuthenticated = true,
    isLoading = false,
    openTrailersInYoutube = true,
    includeSpecials = true,
    versionName = "1.0.0",
)

internal val appearanceState = loggedInState.copy(currentPage = SettingsPage.APPEARANCE, currentPageTitle = "Appearance")
internal val behaviorState = loggedInState.copy(currentPage = SettingsPage.BEHAVIOR, currentPageTitle = "Behavior")
internal val notificationsState = loggedInState.copy(currentPage = SettingsPage.NOTIFICATIONS, currentPageTitle = "Notifications")
internal val privacyState = loggedInState.copy(currentPage = SettingsPage.PRIVACY, currentPageTitle = "Privacy")
internal val infoState = loggedInState.copy(currentPage = SettingsPage.INFO, currentPageTitle = "Info")
internal val licensesState = loggedInState.copy(currentPage = SettingsPage.LICENSES, currentPageTitle = "Licenses & Attribution")
internal val traktState = loggedInState.copy(currentPage = SettingsPage.TRAKT, currentPageTitle = "Trakt Account")
internal val traktLoggedOutState = defaultState.copy(
    currentPage = SettingsPage.TRAKT,
    currentPageTitle = "Trakt Account",
    labels = loggedOutTraktLabels,
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
                behaviorState,
            )
        }
}
