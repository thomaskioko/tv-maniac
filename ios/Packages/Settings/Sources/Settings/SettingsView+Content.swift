import Components
import DesignSystem
import Models
import SwiftUI
import TvManiac
import TvManiacKit
import UserNotifications

extension SettingsView {
    // MARK: - Behavior Toggles

    var behaviorToggles: [SettingsToggleItem] {
        var toggles: [SettingsToggleItem] = []

        toggles.append(SettingsToggleItem(
            id: "sync",
            icon: "arrow.triangle.2.circlepath",
            title: uiState.labels.syncTitle,
            subtitle: uiState.labels.syncDescription,
            secondarySubtitle: uiState.labels.lastSync,
            isOn: uiState.backgroundSyncEnabled,
            onToggle: { presenter.dispatch(action: BackgroundSyncToggled(enabled: $0)) }
        ))

        toggles.append(SettingsToggleItem(
            id: "specials",
            icon: "film.stack",
            title: uiState.labels.includeSpecialsTitle,
            subtitle: uiState.labels.includeSpecialsDescription,
            isOn: uiState.includeSpecials,
            onToggle: { presenter.dispatch(action: IncludeSpecialsToggled(enabled: $0)) }
        ))

        toggles.append(SettingsToggleItem(
            id: "quick-rate",
            icon: "star.fill",
            title: uiState.labels.quickRateTitle,
            subtitle: uiState.labels.quickRateDescription,
            isOn: uiState.quickRateEnabled,
            isLocked: uiState.premium.quickRateLocked,
            lockedBadgeText: uiState.premium.badgeText,
            lockedAccessibilityLabel: uiState.premium.lockedContentDescription,
            onToggle: { presenter.dispatch(action: QuickRateToggled(enabled: $0)) }
        ))

        toggles.append(SettingsToggleItem(
            id: "youtube",
            icon: "tv",
            title: uiState.labels.youtubeTitle,
            subtitle: uiState.labels.youtubeDescription,
            isOn: uiState.openTrailersInYoutube,
            onToggle: { presenter.dispatch(action: YoutubeToggled(enabled: $0)) }
        ))

        return toggles
    }

    // MARK: - Notification Toggles

    var notificationToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "notifications",
                icon: "bell.fill",
                title: uiState.labels.episodeNotificationsTitle,
                subtitle: uiState.labels.episodeNotificationsDescription,
                isOn: uiState.episodeNotificationsEnabled,
                isLocked: uiState.premium.episodeNotificationsLocked,
                lockedBadgeText: uiState.premium.badgeText,
                lockedAccessibilityLabel: uiState.premium.lockedContentDescription,
                onToggle: { handleNotificationToggle(enabled: $0) }
            ),
        ]
    }

    // MARK: - Privacy

    var privacyToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "crash-reporting",
                icon: "ladybug",
                title: uiState.labels.crashReportingTitle,
                subtitle: uiState.labels.crashReportingDescription,
                isOn: uiState.crashReportingEnabled,
                onToggle: { presenter.dispatch(action: CrashReportingToggled(enabled: $0)) }
            ),
        ]
    }

    var privacyLinks: [SettingsNavigationItem] {
        [
            SettingsNavigationItem(
                id: "privacy-policy",
                icon: "hand.raised",
                title: uiState.labels.privacyPolicy,
                onTap: { showPolicy = true }
            ),
        ]
    }

    // MARK: - Info

    var infoContent: SettingsInfoContent {
        SettingsInfoContent(
            icon: TvManiacAppIcon.image(),
            appName: uiState.labels.appName,
            versionText: uiState.labels.version,
            description: uiState.labels.aboutDescription,
            sourceCodeLabel: uiState.labels.sourceCode,
            sourceCodeValue: uiState.labels.github,
            apiDisclaimer: uiState.labels.apiDisclaimer,
            onVersionTap: { presenter.dispatch(action: VersionClicked()) },
            onSourceCodeTap: {
                if let url = URL(string: uiState.githubUrl) {
                    openURL(url)
                }
            }
        )
    }

    // MARK: - Licenses

    var licenseSections: [SettingsLicenseSection] {
        [
            SettingsLicenseSection(
                id: "app",
                label: uiState.labels.licensesApp,
                items: [
                    SettingsLinkItem(
                        id: "tvmaniac",
                        title: uiState.labels.appName,
                        body: uiState.labels.aboutDescription,
                        link: uiState.githubUrl,
                        onOpen: {
                            if let url = URL(string: uiState.githubUrl) { openURL(url) }
                        }
                    ),
                ]
            ),
            SettingsLicenseSection(
                id: "data",
                label: uiState.labels.licensesData,
                items: [
                    SettingsLinkItem(
                        id: "tmdb",
                        leadingAsset: "TmdbLogo",
                        title: uiState.labels.tmdbTitle,
                        body: uiState.labels.tmdbBody,
                        link: tmdbURL,
                        onOpen: {
                            if let url = URL(string: tmdbURL) { openURL(url) }
                        }
                    ),
                    SettingsLinkItem(
                        id: "trakt",
                        leadingAsset: "TraktLogo",
                        title: uiState.labels.traktTitle,
                        body: uiState.labels.traktBody,
                        link: traktURL,
                        onOpen: {
                            if let url = URL(string: traktURL) { openURL(url) }
                        }
                    ),
                ]
            ),
        ]
    }

    // MARK: - Trakt

    var accountContent: SettingsAccountContent {
        SettingsAccountContent(
            title: uiState.labels.traktTitle,
            description: uiState.labels.traktDescription,
            authenticationLabel: uiState.labels.traktAuthentication,
            connectTitle: uiState.labels.connectTitle,
            syncDescription: uiState.labels.accountSyncDescription,
            connectedTitle: uiState.labels.traktConnected,
            connectedDescription: uiState.accountConnectedDescription ?? uiState.labels.traktConnectedDescription,
            isAuthenticated: uiState.isAuthenticated,
            isProcessingAuth: uiState.isProcessingAuth,
            logoutLabel: uiState.labels.logout,
            loginLabel: uiState.labels.login,
            providerName: uiState.activeProviderName ?? "",
            providerLogoName: uiState.activeProvider?.logoAssetName ?? SyncProviderSource.trakt.logoAssetName,
            authProviders: uiState.authProviders.map { option in
                SwiftAuthProvider(
                    id: option.provider.name,
                    label: option.label,
                    logoName: option.provider.logoAssetName
                )
            },
            switchTargetLogoName: uiState.switchTargetProvider?.logoAssetName,
            switchActionLabel: uiState.switchActionLabel,
            isSwitching: uiState.isSwitching,
            showSwitchConfirmation: showingSwitchAlert,
            switchDialogTitle: uiState.switchDialogTitle,
            switchDialogMessage: uiState.switchDialogMessage,
            switchConfirmLabel: uiState.labels.switchConfirm,
            switchCancelLabel: uiState.labels.switchCancel,
            switchingLabel: uiState.labels.switching,
            onLogout: { showingLogoutAlert = true },
            onProviderSelected: { id in
                presenter.dispatch(action: AccountLoginClicked(provider: id == "SIMKL" ? .simkl : .trakt))
            },
            onSwitchProvider: {
                if let target = uiState.switchTargetProvider {
                    presenter.dispatch(action: SwitchProviderClicked(provider: target))
                }
            },
            onConfirmSwitch: { presenter.dispatch(action: ConfirmSwitchDiscard()) },
            onDismissSwitchDialog: { presenter.dispatch(action: DismissSwitchDialog()) }
        )
    }

    // MARK: - Notification Handling

    func handleNotificationToggle(enabled: Bool) {
        guard enabled else {
            presenter.dispatch(action: EpisodeNotificationsToggled(enabled: false))
            return
        }

        Task {
            let settings = await UNUserNotificationCenter.current().notificationSettings()
            await MainActor.run {
                if settings.authorizationStatus == .denied {
                    showNotificationPermissionDeniedAlert = true
                } else {
                    presenter.dispatch(action: EpisodeNotificationsToggled(enabled: true))
                }
            }
        }
    }

    // MARK: - Helpers

    func imageQualityTitle(for quality: SwiftImageQuality) -> String {
        switch quality {
        case .auto:
            uiState.labels.imageQualityAuto
        case .high:
            uiState.labels.imageQualityHigh
        case .medium:
            uiState.labels.imageQualityMedium
        case .low:
            uiState.labels.imageQualityLow
        }
    }
}
