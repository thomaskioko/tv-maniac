package com.thomaskioko.tvmaniac.settings.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel

internal val defaultState = SettingsState(
    theme = ThemeModel.DARK,
    imageQuality = ImageQuality.HIGH,
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
    showthemePopup = false,
    showTraktDialog = false,
    showAboutDialog = false,
    showLogoutDialog = false,
    isAuthenticated = true,
    openTrailersInYoutube = true,
    includeSpecials = true,
    versionName = "1.0.0",
)

internal val appearanceState = loggedInState.copy(currentPage = SettingsPage.APPEARANCE)
internal val behaviorState = loggedInState.copy(currentPage = SettingsPage.BEHAVIOR)
internal val notificationsState = loggedInState.copy(currentPage = SettingsPage.NOTIFICATIONS)
internal val privacyState = loggedInState.copy(currentPage = SettingsPage.PRIVACY)
internal val infoState = loggedInState.copy(currentPage = SettingsPage.INFO)
internal val licensesState = loggedInState.copy(currentPage = SettingsPage.LICENSES)
internal val traktState = loggedInState.copy(currentPage = SettingsPage.TRAKT)

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
