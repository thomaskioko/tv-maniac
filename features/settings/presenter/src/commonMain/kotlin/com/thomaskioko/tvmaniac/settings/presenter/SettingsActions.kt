package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality

public sealed interface SettingsActions

public data class ThemeSelected(
    val theme: ThemeModel,
) : SettingsActions

public data object BackClicked : SettingsActions

public data class OpenSettingsPage(
    val page: SettingsPage,
) : SettingsActions

public data object ShowTraktDialog : SettingsActions

public data object DismissTraktDialog : SettingsActions

public data object TraktLogoutClicked : SettingsActions

public data class TraktLoginClicked(val provider: AccountProvider) : SettingsActions

public data class ImageQualitySelected(
    val quality: ImageQuality,
) : SettingsActions

public data class YoutubeToggled(
    val enabled: Boolean,
) : SettingsActions

public data object VersionClicked : SettingsActions

public data class IncludeSpecialsToggled(
    val enabled: Boolean,
) : SettingsActions

public data class BackgroundSyncToggled(
    val enabled: Boolean,
) : SettingsActions

public data class EpisodeNotificationsToggled(
    val enabled: Boolean,
) : SettingsActions

public data class CrashReportingToggled(
    val enabled: Boolean,
) : SettingsActions

public data class SettingsMessageShown(val id: Long) : SettingsActions
