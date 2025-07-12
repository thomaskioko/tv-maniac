package com.thomaskioko.tvmaniac.settings.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.UserInfo

class SettingsPreviewParameterProvider : PreviewParameterProvider<SettingsState> {
    override val values: Sequence<SettingsState>
        get() {
            return sequenceOf(
                SettingsState(
                    appTheme = AppTheme.DARK_THEME,
                    isLoading = false,
                    showthemePopup = false,
                    showTraktDialog = false,
                    errorMessage = null,
                    showLogoutDialog = false,
                    userInfo = UserInfo(
                        slug = "me",
                        userName = "@j_Doe",
                        fullName = "J Doe",
                        userPicUrl = "image.png",
                    ),
                ),
                SettingsState(
                    appTheme = AppTheme.DARK_THEME,
                    isLoading = false,
                    showthemePopup = false,
                    showTraktDialog = false,
                    errorMessage = null,
                    showLogoutDialog = false,
                    userInfo = null,
                ),
            )
        }
}
