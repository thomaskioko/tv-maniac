package com.thomaskioko.tvmaniac.settings.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState

val defaultState = SettingsState(
    appTheme = AppTheme.DARK_THEME,
    imageQuality = ImageQuality.HIGH,
    showthemePopup = false,
    showImageQualityDialog = false,
    showTraktDialog = false,
    errorMessage = null,
    showLogoutDialog = false,
    isAuthenticated = false,
)

val loggedInState = SettingsState(
    appTheme = AppTheme.DARK_THEME,
    imageQuality = ImageQuality.MEDIUM,
    showthemePopup = false,
    showImageQualityDialog = false,
    showTraktDialog = false,
    errorMessage = null,
    showLogoutDialog = false,
    isAuthenticated = true,
)

class SettingsPreviewParameterProvider : PreviewParameterProvider<SettingsState> {
    override val values: Sequence<SettingsState>
        get() {
            return sequenceOf(
                defaultState,
                loggedInState,
            )
        }
}
