package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.datastore.api.ImageQuality

public sealed interface SettingsActions

public data class ThemeSelected(
    val theme: ThemeModel,
) : SettingsActions

public data object ChangeThemeClicked : SettingsActions

public data object BackClicked : SettingsActions

public data object DismissThemeClicked : SettingsActions

public data object ShowTraktDialog : SettingsActions

public data object DismissTraktDialog : SettingsActions

public data object TraktLogoutClicked : SettingsActions

public data class ImageQualitySelected(
    val quality: ImageQuality,
) : SettingsActions

public data class YoutubeToggled(
    val enabled: Boolean,
) : SettingsActions

public data object ShowAboutDialog : SettingsActions

public data object DismissAboutDialog : SettingsActions
