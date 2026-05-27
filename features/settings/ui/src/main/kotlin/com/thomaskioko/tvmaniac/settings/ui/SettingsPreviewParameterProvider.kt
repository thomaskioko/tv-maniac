package com.thomaskioko.tvmaniac.settings.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.SettingsCategoryGroup
import com.thomaskioko.tvmaniac.settings.presenter.SettingsCategoryItem
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

internal val defaultState = SettingsState(
    theme = ThemeModel.DARK,
    imageQuality = ImageQuality.HIGH,
    currentPageTitle = "Settings",
    rootGroups = previewRootGroups(authenticated = false),
    showthemePopup = false,
    showTraktDialog = false,
    showAboutDialog = false,
    showLogoutDialog = false,
    isAuthenticated = false,
    openTrailersInYoutube = false,
    includeSpecials = false,
    versionName = "1.0.0",
)

internal val loggedInState = SettingsState(
    theme = ThemeModel.DARK,
    imageQuality = ImageQuality.MEDIUM,
    currentPageTitle = "Settings",
    rootGroups = previewRootGroups(authenticated = true),
    showthemePopup = false,
    showTraktDialog = false,
    showAboutDialog = false,
    showLogoutDialog = false,
    isAuthenticated = true,
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

internal class SettingsPreviewParameterProvider : PreviewParameterProvider<SettingsState> {
    override val values: Sequence<SettingsState>
        get() {
            return sequenceOf(
                defaultState,
                loggedInState,
                appearanceState,
                behaviorState,
            )
        }
}
