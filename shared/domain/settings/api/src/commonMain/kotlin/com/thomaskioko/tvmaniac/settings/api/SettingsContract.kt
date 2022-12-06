package com.thomaskioko.tvmaniac.settings.api

import com.thomaskioko.tvmaniac.shared.core.ui.Action
import com.thomaskioko.tvmaniac.shared.core.ui.Effect

sealed class SettingsState

data class SettingsContent(
    val theme: Theme,
    val showPopup: Boolean,
    val showTraktDialog: Boolean,
    val loggedIn: Boolean,
    val traktUserName: String,
    val traktFullName: String?,
    val traktUserPicUrl: String?,
) : SettingsState() {
    companion object {
        val DEFAULT = SettingsContent(
            theme = Theme.SYSTEM,
            showPopup = false,
            showTraktDialog = false,
            loggedIn = false,
            traktUserName = "",
            traktFullName = "",
            traktUserPicUrl = ""
        )
    }
}

sealed class SettingsActions : Action {
    data class ThemeSelected(
        val theme: String
    ) : SettingsActions()

    object LoadTheme : SettingsActions()
    object ThemeClicked : SettingsActions()
    object ShowTraktDialog : SettingsActions()
    object DismissTraktDialog : SettingsActions()
    object TraktLogout : SettingsActions()
    object TraktLogin : SettingsActions()
    object RefreshTraktAuthToken : SettingsActions()
    object FetchTraktUserProfile : SettingsActions()
}

sealed class SettingsEffect : Effect

