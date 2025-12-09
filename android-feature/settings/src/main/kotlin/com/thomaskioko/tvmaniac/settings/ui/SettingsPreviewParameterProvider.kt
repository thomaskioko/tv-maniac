package com.thomaskioko.tvmaniac.settings.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel

internal val defaultState = SettingsState(
    theme = ThemeModel.DARK,
    imageQuality = ImageQuality.HIGH,
    showthemePopup = false,
    showTraktDialog = false,
    showAboutDialog = false,
    errorMessage = null,
    showLogoutDialog = false,
    isAuthenticated = false,
    openTrailersInYoutube = false,
)

internal val loggedInState = SettingsState(
    theme = ThemeModel.DARK,
    imageQuality = ImageQuality.MEDIUM,
    showthemePopup = false,
    showTraktDialog = false,
    showAboutDialog = false,
    errorMessage = null,
    showLogoutDialog = false,
    isAuthenticated = true,
    openTrailersInYoutube = true,
)

internal class SettingsPreviewParameterProvider : PreviewParameterProvider<SettingsState> {
    override val values: Sequence<SettingsState>
        get() {
            return sequenceOf(
                defaultState,
                loggedInState,
            )
        }
}
