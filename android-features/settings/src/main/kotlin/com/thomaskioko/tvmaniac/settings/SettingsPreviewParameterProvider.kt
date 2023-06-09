package com.thomaskioko.tvmaniac.settings

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.datastore.api.Theme
import com.thomaskioko.tvmaniac.presentation.settings.Default
import com.thomaskioko.tvmaniac.presentation.settings.LoggedInContent
import com.thomaskioko.tvmaniac.presentation.settings.SettingsState
import com.thomaskioko.tvmaniac.presentation.settings.UserInfo

class SettingsPreviewParameterProvider : PreviewParameterProvider<SettingsState> {
    override val values: Sequence<SettingsState>
        get() {
            return sequenceOf(
                Default(
                    theme = Theme.DARK,
                    showPopup = false,
                    showTraktDialog = false,
                    errorMessage = null,
                    userInfo = null,
                ),
                LoggedInContent(
                    theme = Theme.DARK,
                    isLoading = false,
                    showPopup = false,
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
            )
        }
}
