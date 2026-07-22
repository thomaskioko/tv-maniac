import Components
import DesignSystem
import Models
import SwiftUI
import TvManiac
import TvManiacKit
import UserNotifications

private let tmdbURL = "https://www.themoviedb.org"
private let traktURL = "https://trakt.tv"

public struct SettingsView: View {
    private let presenter: SettingsPresenter
    @StateValue private var uiState: SettingsState
    @StateObject private var store = SettingsAppStorage.shared
    @State private var showingLogoutAlert: Bool = false
    @State private var showingSwitchAlert: Bool = false
    @State private var showingErrorAlert: Bool = false
    @State private var showPolicy = false
    @State private var showNotificationPermissionDeniedAlert = false
    @Environment(\.openURL) var openURL
    @EnvironmentObject private var appDelegate: AppDelegate

    public init(presenter: SettingsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    private var screenState: SettingsScreen<DeviceAppTheme>.State {
        SettingsScreen<DeviceAppTheme>.State(
            isLoading: uiState.isLoading,
            rootTitle: String(\.label_settings_title),
            currentPage: uiState.currentPage.toRoute(),
            rootSections: rootSections,
            themeItem: themeItem,
            imageQualityItem: imageQualityItem,
            layoutToggles: layoutToggles,
            fontSizeItem: fontSizeItem,
            discoverSectionsNavItem: discoverSectionsNavItem,
            discoverSectionToggles: discoverSectionToggles,
            posterStyleNavItem: posterStyleNavItem,
            posterStyleItem: posterStyleItem,
            behaviorToggles: behaviorToggles,
            notificationToggles: notificationToggles,
            privacyToggles: privacyToggles,
            privacyLinks: privacyLinks,
            infoContent: infoContent,
            licenseSections: licenseSections,
            accountContent: accountContent
        )
    }

    public var body: some View {
        TvManiacTypographyScheme.updateFontScale(percent: store.fontSizePercent)
        return SettingsScreen(
            state: screenState,
            onBack: { presenter.dispatch(action: BackClicked___()) }
        )
        .id(store.fontSizePercent)
        .settingsObservers(
            uiState: uiState,
            store: store,
            showingErrorAlert: $showingErrorAlert,
            showingSwitchAlert: $showingSwitchAlert
        )
        .settingsAlerts(
            uiState: uiState,
            showingErrorAlert: $showingErrorAlert,
            showingLogoutAlert: $showingLogoutAlert,
            showingSwitchAlert: $showingSwitchAlert,
            onLogout: { presenter.dispatch(action: AccountLogoutClicked()) },
            onConfirmSwitch: { presenter.dispatch(action: ConfirmSwitchDiscard()) },
            onDismissSwitchDialog: { presenter.dispatch(action: DismissSwitchDialog()) },
            onDismissError: { id in presenter.dispatch(action: SettingsMessageShown(id: id)) }
        )
        .alert(
            String(\.notification_permission_denied_title),
            isPresented: $showNotificationPermissionDeniedAlert
        ) {
            Button(String(\.notification_permission_denied_cancel), role: .cancel) {}
            Button(String(\.notification_permission_denied_settings)) {
                if let settingsUrl = URL(string: UIApplication.openSettingsURLString) {
                    openURL(settingsUrl)
                }
            }
        } message: {
            Text(String(\.notification_permission_denied_message))
        }
        .sheet(isPresented: $showPolicy) {
            if let url = URL(string: uiState.privacyPolicyUrl) {
                SFSafariViewWrapper(url: url)
                    .appTint()
                    .appTheme()
            }
        }
        .onAppear {
            store.imageQuality = uiState.imageQuality.toSwift()
            store.hapticFeedbackEnabled = uiState.hapticFeedbackEnabled
            store.fontSizePercent = Int(uiState.fontSizePercent)
            store.posterWidthScale = Double(uiState.posterWidth.scale)
            store.landscapeWidthScale = Double(uiState.landscapeWidth.scale)
            store.posterCornerRadius = Double(uiState.posterCornerStyle.cornerRadius)
        }
        .onChange(of: uiState.hapticFeedbackEnabled) { _, newValue in
            store.hapticFeedbackEnabled = newValue
        }
        .onChange(of: uiState.fontSizePercent) { _, newValue in
            store.fontSizePercent = Int(newValue)
        }
        .settingsPosterStyleObservers(uiState: uiState, store: store)
    }

    // MARK: - Root Sections

    private var rootSections: [SettingsRootSection] {
        uiState.rootGroups.map { group in
            SettingsRootSection(
                id: group.label,
                label: group.label,
                items: group.items.map { item in
                    let route = item.page.toRoute()
                    return SettingsNavigationItem(
                        id: route.rawValue,
                        icon: route.iconName,
                        title: item.title,
                        subtitle: item.summary,
                        onTap: { presenter.dispatch(action: OpenSettingsPage(page: item.page)) }
                    )
                }
            )
        }
    }

    // MARK: - Theme

    private var themeItem: SettingsThemeItem<DeviceAppTheme> {
        SettingsThemeItem(
            icon: "paintpalette",
            title: uiState.labels.themeTitle,
            subtitle: uiState.labels.themeSubtitle,
            themes: DeviceAppTheme.sortedThemes,
            selectedTheme: store.appTheme,
            isCustomThemesLocked: uiState.locks.customThemesLocked,
            lockedBadgeText: uiState.locks.badgeText,
            lockedTitle: uiState.locks.themesLockedTitle,
            lockedMessage: uiState.locks.themesLockedMessage,
            lockedActionText: uiState.locks.upgradeText,
            lockedAccessibilityLabel: uiState.locks.lockedContentDescription,
            onUpgradeClick: { presenter.dispatch(action: UpgradeToPremiumClicked()) },
            onThemeSelected: { selectedTheme in
                store.appTheme = selectedTheme
                let theme = selectedTheme.toTheme()
                presenter.dispatch(action: ThemeSelected(theme: theme.toThemeModel()))
            }
        )
    }

    // MARK: - Image Quality

    private var imageQualityItem: SettingsImageQualityItem {
        let currentQuality = uiState.imageQuality.toSwift()
        return SettingsImageQualityItem(
            icon: "photo",
            title: uiState.labels.imageQualityTitle,
            subtitle: uiState.labels.imageQualityDescription,
            options: SwiftImageQuality.allCases.map { quality in
                SettingsImageQualityOption(
                    id: quality.rawValue,
                    label: imageQualityTitle(for: quality),
                    onSelect: {
                        let kmpQuality = TvManiac.ImageQuality.fromSwift(quality)
                        presenter.dispatch(action: ImageQualitySelected(quality: kmpQuality))
                        store.imageQuality = quality
                    }
                )
            },
            selectedOptionId: currentQuality.rawValue
        )
    }

    // MARK: - Layout Toggles

    private var layoutToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "haptic",
                icon: "iphone.radiowaves.left.and.right",
                title: uiState.labels.hapticFeedbackTitle,
                subtitle: uiState.labels.hapticFeedbackDescription,
                isOn: uiState.hapticFeedbackEnabled,
                onToggle: { presenter.dispatch(action: HapticFeedbackToggled(enabled: $0)) }
            ),
            SettingsToggleItem(
                id: "season-order",
                icon: "arrow.up.arrow.down",
                title: uiState.labels.seasonOrderTitle,
                subtitle: uiState.labels.seasonOrderDescription,
                isOn: uiState.newestSeasonFirst,
                onToggle: { presenter.dispatch(action: SeasonOrderToggled(enabled: $0)) }
            ),
            SettingsToggleItem(
                id: "blur-unwatched",
                icon: "eye.slash",
                title: uiState.labels.blurUnwatchedTitle,
                subtitle: uiState.labels.blurUnwatchedDescription,
                isOn: uiState.blurImage,
                onToggle: { presenter.dispatch(action: BlurUnwatchedToggled(enabled: $0)) }
            ),
        ]
    }

    // MARK: - Font Size

    private var fontSizeItem: SettingsFontSizeItem {
        SettingsFontSizeItem(
            title: uiState.labels.fontSizeTitle,
            description: uiState.labels.fontSizeDescription,
            previewText: uiState.labels.fontSizePreview,
            resetLabel: uiState.labels.fontSizeReset,
            percent: store.fontSizePercent,
            onPercentChange: { newPercent in
                presenter.dispatch(action: FontSizeChanged(percent: Int32(newPercent)))
                store.fontSizePercent = newPercent
            }
        )
    }

    // MARK: - Discover Sections

    private var discoverSectionsNavItem: SettingsNavigationItem {
        SettingsNavigationItem(
            id: SettingsPageRoute.discoverSections.rawValue,
            icon: SettingsPageRoute.discoverSections.iconName,
            title: uiState.labels.discoverSectionsTitle,
            subtitle: uiState.labels.discoverSectionsDescription,
            onTap: { presenter.dispatch(action: OpenSettingsPage(page: SettingsPage.discoverSections)) }
        )
    }

    private var discoverSectionToggles: [SettingsToggleItem] {
        uiState.discoverSectionToggles.map { toggle in
            SettingsToggleItem(
                id: toggle.section.name,
                icon: discoverSectionIcon(for: toggle.section),
                title: toggle.label,
                subtitle: "",
                isOn: toggle.visible,
                onToggle: { presenter.dispatch(action: DiscoverSectionToggled(section: toggle.section, visible: $0)) }
            )
        }
    }

    private func discoverSectionIcon(for section: ApiDiscoverSection) -> String {
        switch section.name {
        case "START_WATCHING": "play.circle"
        case "TRENDING_TODAY": "flame"
        case "UPCOMING": "calendar"
        case "POPULAR": "star"
        case "TOP_RATED": "trophy"
        default: "square.grid.2x2"
        }
    }

    // MARK: - Poster Style

    private var posterStyleNavItem: SettingsNavigationItem {
        SettingsNavigationItem(
            id: SettingsPageRoute.posterStyle.rawValue,
            icon: SettingsPageRoute.posterStyle.iconName,
            title: uiState.labels.posterStyle.title,
            subtitle: uiState.labels.posterStyle.subtitle,
            onTap: { presenter.dispatch(action: OpenSettingsPage(page: SettingsPage.posterStyle)) }
        )
    }

    private var posterStyleItem: SettingsPosterStyleItem {
        let labels = uiState.labels.posterStyle
        return SettingsPosterStyleItem(
            title: labels.title,
            description: labels.subtitle,
            livePreviewLabel: labels.livePreview,
            resetLabel: labels.reset,
            postersLabel: labels.postersLabel,
            landscapeLabel: labels.landscapeLabel,
            cornerLabel: labels.cornerLabel,

            postersOptions: posterWidthOptions(keyPrefix: "posters") { width in
                presenter.dispatch(action: PosterWidthSelected(width: width))
                store.posterWidthScale = Double(width.scale)
            },
            landscapeOptions: posterWidthOptions(keyPrefix: "landscape") { width in
                presenter.dispatch(action: LandscapeWidthSelected(width: width))
                store.landscapeWidthScale = Double(width.scale)
            },
            cornerOptions: ApiPosterCornerStyle.entries.map { style in
                SettingsPosterStyleOption(
                    id: style.name,
                    label: cornerStyleLabel(style),
                    onSelect: {
                        presenter.dispatch(action: PosterCornerStyleSelected(style: style))
                        store.posterCornerRadius = Double(style.cornerRadius)
                    }
                )
            },
            selectedPostersId: "posters-\(uiState.posterWidth.name)",
            selectedLandscapeId: "landscape-\(uiState.landscapeWidth.name)",
            selectedCornerId: uiState.posterCornerStyle.name,
            posterScale: CGFloat(uiState.posterWidth.scale),
            landscapeScale: CGFloat(uiState.landscapeWidth.scale),
            cornerRadius: CGFloat(uiState.posterCornerStyle.cornerRadius),
            isLocked: uiState.locks.posterStyleLocked,
            lockedBadgeText: uiState.locks.badgeText,
            lockedActionText: uiState.locks.upgradeText,
            lockedAccessibilityLabel: uiState.locks.lockedContentDescription,
            onUpgradeClick: { presenter.dispatch(action: UpgradeToPremiumClicked()) },
            onReset: {
                presenter.dispatch(action: PosterStyleReset())
                store.posterWidthScale = 1
                store.landscapeWidthScale = 1
                store.posterCornerRadius = 0
            }
        )
    }

    private func posterWidthOptions(keyPrefix: String, onSelect: @escaping (ApiPosterWidth) -> Void) -> [SettingsPosterStyleOption] {
        ApiPosterWidth.entries.map { width in
            SettingsPosterStyleOption(
                id: "\(keyPrefix)-\(width.name)",
                label: widthLabel(width),
                onSelect: { onSelect(width) }
            )
        }
    }

    private func widthLabel(_ width: ApiPosterWidth) -> String {
        let labels = uiState.labels.posterStyle
        return switch width.name {
        case "COMPACT": labels.widthCompact
        case "STANDARD": labels.widthStandard
        case "COMFORTABLE": labels.widthComfortable
        case "LARGE": labels.widthLarge
        default: width.name
        }
    }

    private func cornerStyleLabel(_ style: ApiPosterCornerStyle) -> String {
        let labels = uiState.labels.posterStyle
        return switch style.name {
        case "SHARP": labels.cornerSharp
        case "CLASSIC": labels.cornerClassic
        case "ROUNDED": labels.cornerRounded
        case "PILL": labels.cornerPill
        default: style.name
        }
    }

    // MARK: - Behavior Toggles

    private var behaviorToggles: [SettingsToggleItem] {
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

    private var notificationToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "notifications",
                icon: "bell.fill",
                title: uiState.labels.episodeNotificationsTitle,
                subtitle: uiState.labels.episodeNotificationsDescription,
                isOn: uiState.episodeNotificationsEnabled,
                isLocked: uiState.locks.episodeNotificationsLocked,
                lockedBadgeText: uiState.locks.badgeText,
                lockedAccessibilityLabel: uiState.locks.lockedContentDescription,
                onToggle: { handleNotificationToggle(enabled: $0) }
            ),
        ]
    }

    // MARK: - Privacy

    private var privacyToggles: [SettingsToggleItem] {
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

    private var privacyLinks: [SettingsNavigationItem] {
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

    private var infoContent: SettingsInfoContent {
        SettingsInfoContent(
            icon: TvManiacAppIcon.image(isDebug: appDelegate.isDebug),
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

    private var licenseSections: [SettingsLicenseSection] {
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

    private var accountContent: SettingsAccountContent {
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
            providerName: providerDisplayName(uiState.activeProvider),
            providerLogoName: uiState.activeProvider?.name == "SIMKL" ? "SimklMono" : "TraktMono",
            authProviders: uiState.authProviders.map { option in
                SwiftAuthProvider(
                    id: option.provider.name,
                    label: option.label,
                    logoName: option.provider.name == "SIMKL" ? "SimklMono" : "TraktMono"
                )
            },
            switchTargetLogoName: uiState.switchTargetProvider?
                .name == "SIMKL" ? "SimklMono" : (uiState.switchTargetProvider != nil ? "TraktMono" : nil),
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

    private func providerDisplayName(_ provider: SyncProviderSource?) -> String {
        provider?.name == "SIMKL" ? "Simkl" : "Trakt"
    }

    // MARK: - Notification Handling

    private func handleNotificationToggle(enabled: Bool) {
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

    private func imageQualityTitle(for quality: SwiftImageQuality) -> String {
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

private extension View {
    func settingsObservers(
        uiState: SettingsState,
        store: SettingsAppStorage,
        showingErrorAlert: Binding<Bool>,
        showingSwitchAlert: Binding<Bool>
    ) -> some View {
        onChange(of: uiState.theme) { _, newTheme in
            store.appTheme = newTheme.toDeviceAppTheme()
        }
        .onChange(of: uiState.imageQuality) { _, imageQuality in
            store.imageQuality = imageQuality.toSwift()
        }
        .onChange(of: uiState.message) { _, message in
            showingErrorAlert.wrappedValue = message != nil
        }
        .onChange(of: uiState.showSwitchConfirmation) { _, show in
            showingSwitchAlert.wrappedValue = show
        }
    }

    func settingsPosterStyleObservers(uiState: SettingsState, store: SettingsAppStorage) -> some View {
        onChange(of: uiState.posterWidth) { _, newValue in
            store.posterWidthScale = Double(newValue.scale)
        }
        .onChange(of: uiState.landscapeWidth) { _, newValue in
            store.landscapeWidthScale = Double(newValue.scale)
        }
        .onChange(of: uiState.posterCornerStyle) { _, newValue in
            store.posterCornerRadius = Double(newValue.cornerRadius)
        }
    }

    func settingsAlerts(
        uiState: SettingsState,
        showingErrorAlert: Binding<Bool>,
        showingLogoutAlert: Binding<Bool>,
        showingSwitchAlert: Binding<Bool>,
        onLogout: @escaping () -> Void,
        onConfirmSwitch: @escaping () -> Void,
        onDismissSwitchDialog: @escaping () -> Void,
        onDismissError: @escaping (Int64) -> Void
    ) -> some View {
        alert(isPresented: showingErrorAlert) {
            Alert(
                title: Text(String(\.label_error)),
                message: Text(uiState.message?.message ?? String(\.error_generic)),
                dismissButton: .default(Text(String(\.label_ok))) {
                    if let message = uiState.message {
                        onDismissError(message.id)
                    }
                }
            )
        }
        .alert(isPresented: showingLogoutAlert) {
            Alert(
                title: Text(String(\.trakt_dialog_logout_title)),
                message: Text(String(\.trakt_dialog_logout_message)),
                primaryButton: .destructive(Text(String(\.logout))) {
                    onLogout()
                },
                secondaryButton: .cancel()
            )
        }
        .alert(
            uiState.switchDialogTitle ?? "",
            isPresented: showingSwitchAlert,
            actions: {
                Button(uiState.labels.switchConfirm, role: .destructive) {
                    onConfirmSwitch()
                }
                Button(uiState.labels.switchCancel, role: .cancel) {
                    onDismissSwitchDialog()
                }
            },
            message: {
                if let message = uiState.switchDialogMessage {
                    Text(message)
                }
            }
        )
    }
}
