package com.thomaskioko.tvmaniac.presentation.settings

import com.thomaskioko.tvmaniac.datastore.api.Theme

sealed class SettingsActions
data class ThemeSelected(
    val theme: Theme,
) : SettingsActions()

data object ChangeThemeClicked : SettingsActions()
data object DismissThemeClicked : SettingsActions()
data object ShowTraktDialog : SettingsActions()
data object DismissTraktDialog : SettingsActions()
data object TraktLogoutClicked : SettingsActions()
data object TraktLoginClicked : SettingsActions()
