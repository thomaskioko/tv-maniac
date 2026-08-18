package com.thomaskioko.tvmaniac.settings.presenter

import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
public class SettingsLabelsMapper(
    private val localizer: Localizer,
) {

    private fun imageQualityDescriptionKey(quality: ImageQuality): StringResourceKey = when (quality) {
        ImageQuality.AUTO -> StringResourceKey.LabelSettingsImageQualityAutoDescription
        ImageQuality.HIGH -> StringResourceKey.LabelSettingsImageQualityHighDescription
        ImageQuality.MEDIUM -> StringResourceKey.LabelSettingsImageQualityMediumDescription
        ImageQuality.LOW -> StringResourceKey.LabelSettingsImageQualityLowDescription
    }

    public operator fun invoke(
        imageQuality: ImageQuality,
        showLastSyncDate: Boolean,
        lastSyncDate: String?,
        versionName: String,
        username: String?,
        isAuthenticated: Boolean,
    ): SettingsLabels = SettingsLabels(
        back = localizer.getString(StringResourceKey.CdBack),
        themeTitle = localizer.getString(StringResourceKey.SettingsThemeSelectorTitle),
        themeSubtitle = localizer.getString(StringResourceKey.SettingsThemeSelectorSubtitle),
        imageQualityTitle = localizer.getString(StringResourceKey.LabelSettingsImageQuality),
        imageQualityDescription = localizer.getString(imageQualityDescriptionKey(imageQuality)),
        imageQualityAuto = localizer.getString(StringResourceKey.LabelSettingsImageQualityAuto),
        imageQualityHigh = localizer.getString(StringResourceKey.LabelSettingsImageQualityHigh),
        imageQualityMedium = localizer.getString(StringResourceKey.LabelSettingsImageQualityMedium),
        imageQualityLow = localizer.getString(StringResourceKey.LabelSettingsImageQualityLow),
        syncTitle = localizer.getString(StringResourceKey.LabelSettingsSyncUpdate),
        syncDescription = localizer.getString(StringResourceKey.LabelSettingsSyncUpdateDescription),
        lastSync = if (showLastSyncDate && lastSyncDate != null) {
            localizer.getString(StringResourceKey.LabelSettingsLastSyncDate, lastSyncDate)
        } else {
            null
        },
        includeSpecialsTitle = localizer.getString(StringResourceKey.LabelSettingsIncludeSpecials),
        includeSpecialsDescription = localizer.getString(StringResourceKey.LabelSettingsIncludeSpecialsDescription),
        quickRateTitle = localizer.getString(StringResourceKey.LabelSettingsQuickRate),
        quickRateDescription = localizer.getString(StringResourceKey.LabelSettingsQuickRateDescription),
        multiplePlaysTitle = localizer.getString(StringResourceKey.LabelSettingsMultiplePlays),
        multiplePlaysDescription = localizer.getString(StringResourceKey.LabelSettingsMultiplePlaysDescription),
        youtubeTitle = localizer.getString(StringResourceKey.LabelSettingsYoutube),
        youtubeDescription = localizer.getString(StringResourceKey.LabelSettingsYoutubeDescription),
        episodeNotificationsTitle = localizer.getString(StringResourceKey.LabelSettingsEpisodeNotifications),
        episodeNotificationsDescription = localizer.getString(StringResourceKey.LabelSettingsEpisodeNotificationsDescription),
        crashReportingTitle = localizer.getString(StringResourceKey.LabelSettingsCrashReporting),
        crashReportingDescription = localizer.getString(StringResourceKey.LabelSettingsCrashReportingDescription),
        hapticFeedbackTitle = localizer.getString(StringResourceKey.SettingsHapticFeedbackTitle),
        hapticFeedbackDescription = localizer.getString(StringResourceKey.SettingsHapticFeedbackDescription),
        seasonOrderTitle = localizer.getString(StringResourceKey.SettingsSeasonOrderTitle),
        seasonOrderDescription = localizer.getString(StringResourceKey.SettingsSeasonOrderDescription),
        blurUnwatchedTitle = localizer.getString(StringResourceKey.SettingsBlurUnwatchedTitle),
        blurUnwatchedDescription = localizer.getString(StringResourceKey.SettingsBlurUnwatchedDescription),
        discoverSectionsTitle = localizer.getString(StringResourceKey.SettingsDiscoverSectionsTitle),
        discoverSectionsDescription = localizer.getString(StringResourceKey.SettingsDiscoverSectionsDescription),
        fontSizeTitle = localizer.getString(StringResourceKey.SettingsFontSizeTitle),
        fontSizeDescription = localizer.getString(StringResourceKey.SettingsFontSizeDescription),
        fontSizePreview = localizer.getString(StringResourceKey.SettingsFontSizePreview),
        fontSizeReset = localizer.getString(StringResourceKey.SettingsFontSizeReset),
        posterStyle = PosterStyleLabels(
            title = localizer.getString(StringResourceKey.SettingsPosterStyleTitle),
            subtitle = localizer.getString(StringResourceKey.SettingsPosterStyleDescription),
            livePreview = localizer.getString(StringResourceKey.SettingsPosterLivePreview),
            reset = localizer.getString(StringResourceKey.SettingsPosterReset),
            postersLabel = localizer.getString(StringResourceKey.SettingsPosterPostersLabel),
            landscapeLabel = localizer.getString(StringResourceKey.SettingsPosterLandscapeLabel),
            cornerLabel = localizer.getString(StringResourceKey.SettingsPosterCornerLabel),
            widthCompact = localizer.getString(StringResourceKey.SettingsPosterWidthCompact),
            widthStandard = localizer.getString(StringResourceKey.SettingsPosterWidthStandard),
            widthComfortable = localizer.getString(StringResourceKey.SettingsPosterWidthComfortable),
            widthLarge = localizer.getString(StringResourceKey.SettingsPosterWidthLarge),
            cornerSharp = localizer.getString(StringResourceKey.SettingsPosterCornerSharp),
            cornerClassic = localizer.getString(StringResourceKey.SettingsPosterCornerClassic),
            cornerRounded = localizer.getString(StringResourceKey.SettingsPosterCornerRounded),
            cornerPill = localizer.getString(StringResourceKey.SettingsPosterCornerPill),
        ),
        privacyPolicy = localizer.getString(StringResourceKey.LabelSettingsPrivacyPolicy),
        appName = localizer.getString(StringResourceKey.SettingsAboutAppName),
        version = localizer.getString(StringResourceKey.SettingsAboutVersion, versionName),
        aboutDescription = localizer.getString(StringResourceKey.SettingsAboutDescription),
        sourceCode = localizer.getString(StringResourceKey.SettingsAboutSourceCode),
        github = localizer.getString(StringResourceKey.SettingsAboutGithub),
        apiDisclaimer = localizer.getString(StringResourceKey.SettingsAboutApiDisclaimer),
        licensesApp = localizer.getString(StringResourceKey.LabelSettingsLicensesSectionApp),
        licensesData = localizer.getString(StringResourceKey.LabelSettingsLicensesSectionData),
        tmdbTitle = localizer.getString(StringResourceKey.LabelSettingsLicensesTmdbTitle),
        tmdbBody = localizer.getString(StringResourceKey.LabelSettingsLicensesTmdbBody),
        traktBody = localizer.getString(StringResourceKey.LabelSettingsLicensesTraktBody),
        traktTitle = localizer.getString(StringResourceKey.SettingsTitleTraktApp),
        traktDescription = localizer.getString(StringResourceKey.TraktDescription),
        traktAuthentication = localizer.getString(StringResourceKey.LabelSettingsTraktAuthentication),
        connectTitle = localizer.getString(StringResourceKey.LabelSettingsConnectTitle),
        accountSyncDescription = localizer.getString(StringResourceKey.LabelSettingsAccountSyncDescription),
        traktConnected = when {
            !isAuthenticated -> localizer.getString(StringResourceKey.LabelSettingsTraktConnect)
            username != null -> localizer.getString(StringResourceKey.LabelSettingsTraktConnectedAs, username)
            else -> localizer.getString(StringResourceKey.LabelSettingsTraktConnected)
        },
        traktConnectedDescription = if (isAuthenticated) {
            localizer.getString(StringResourceKey.LabelSettingsTraktConnectedDescription)
        } else {
            localizer.getString(StringResourceKey.SettingsTraktDetailDescription)
        },
        logout = localizer.getString(StringResourceKey.Logout),
        login = localizer.getString(StringResourceKey.Login),
        switchConfirm = localizer.getString(StringResourceKey.LabelAccountSwitchDialogConfirm),
        switchCancel = localizer.getString(StringResourceKey.LabelSettingsTraktDialogButtonSecondary),
        switching = localizer.getString(StringResourceKey.LabelAccountSwitching),
    )
}
