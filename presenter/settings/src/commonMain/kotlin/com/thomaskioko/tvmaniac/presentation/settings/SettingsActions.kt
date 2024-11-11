package com.thomaskioko.tvmaniac.presentation.settings

import com.thomaskioko.tvmaniac.datastore.api.AppTheme

sealed class SettingsActions

data class ThemeSelected(
  val appTheme: AppTheme,
) : SettingsActions()

data object ChangeThemeClicked : SettingsActions()

data object DismissThemeClicked : SettingsActions()

data object ShowTraktDialog : SettingsActions()

data object DismissTraktDialog : SettingsActions()

data object TraktLogoutClicked : SettingsActions()

data object TraktLoginClicked : SettingsActions()
