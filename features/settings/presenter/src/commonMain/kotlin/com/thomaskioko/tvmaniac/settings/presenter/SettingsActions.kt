package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.datastore.api.PosterCornerStyle
import com.thomaskioko.tvmaniac.datastore.api.PosterWidth
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality

public sealed interface SettingsActions

public data class ThemeSelected(
    val theme: ThemeModel,
) : SettingsActions

public data object BackClicked : SettingsActions

public data object UpgradeToPremiumClicked : SettingsActions

public data class OpenSettingsPage(
    val page: SettingsPage,
) : SettingsActions

public data object ShowLogoutDialog : SettingsActions

public data object DismissLogoutDialog : SettingsActions

public data object AccountLogoutClicked : SettingsActions

public data class AccountLoginClicked(val provider: SyncProviderSource) : SettingsActions

public data class SwitchProviderClicked(val provider: SyncProviderSource) : SettingsActions

public data object ConfirmSwitchDiscard : SettingsActions

public data object DismissSwitchDialog : SettingsActions

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

public data class HapticFeedbackToggled(
    val enabled: Boolean,
) : SettingsActions

public data class SeasonOrderToggled(
    val enabled: Boolean,
) : SettingsActions

public data class BlurUnwatchedToggled(
    val enabled: Boolean,
) : SettingsActions

public data class DiscoverSectionToggled(
    val section: DiscoverSection,
    val visible: Boolean,
) : SettingsActions

public data class FontSizeChanged(
    val percent: Int,
) : SettingsActions

public data class PosterWidthSelected(
    val width: PosterWidth,
) : SettingsActions

public data class LandscapeWidthSelected(
    val width: PosterWidth,
) : SettingsActions

public data class PosterCornerStyleSelected(
    val style: PosterCornerStyle,
) : SettingsActions

public data object PosterStyleReset : SettingsActions

public data class SettingsMessageShown(val id: Long) : SettingsActions
