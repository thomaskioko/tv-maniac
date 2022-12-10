package com.thomaskioko.tvmaniac.settings.api


sealed class SettingsActions
data class ThemeSelected(
    val theme: Theme
) : SettingsActions()

object ChangeThemeClicked : SettingsActions()
object DimissThemeClicked : SettingsActions()
object ShowTraktDialog : SettingsActions()
object DismissTraktDialog : SettingsActions()
object TraktLogout : SettingsActions()
object TraktLogin : SettingsActions()