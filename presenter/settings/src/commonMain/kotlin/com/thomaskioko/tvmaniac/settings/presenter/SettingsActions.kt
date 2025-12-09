package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.datastore.api.ImageQuality

sealed interface SettingsActions

data class ThemeSelected(
    val theme: ThemeModel,
) : SettingsActions

data object ChangeThemeClicked : SettingsActions

data object BackClicked : SettingsActions

data object DismissThemeClicked : SettingsActions

data object ShowTraktDialog : SettingsActions

data object DismissTraktDialog : SettingsActions

data object TraktLogoutClicked : SettingsActions

data class ImageQualitySelected(
    val quality: ImageQuality,
) : SettingsActions

data class YoutubeToggled(
    val enabled: Boolean,
) : SettingsActions

data object ShowAboutDialog : SettingsActions

data object DismissAboutDialog : SettingsActions
