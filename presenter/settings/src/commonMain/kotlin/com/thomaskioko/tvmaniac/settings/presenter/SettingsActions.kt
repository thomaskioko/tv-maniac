package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.ImageQuality

sealed interface SettingsActions

data class ThemeSelected(
    val appTheme: AppTheme,
) : SettingsActions

data object ChangeThemeClicked : SettingsActions

data object BackClicked : SettingsActions

data object DismissThemeClicked : SettingsActions

data object ShowTraktDialog : SettingsActions

data object DismissTraktDialog : SettingsActions

data object TraktLogoutClicked : SettingsActions

data object ShowImageQualityDialog : SettingsActions

data object DismissImageQualityDialog : SettingsActions

data class ImageQualitySelected(
    val quality: ImageQuality,
) : SettingsActions
