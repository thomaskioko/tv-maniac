import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit
import UserNotifications

struct SettingsView: View {
    private let presenter: SettingsPresenter
    @StateObject @KotlinStateFlow private var uiState: SettingsState
    @StateObject private var store = SettingsAppStorage.shared
    @State private var showingLogoutAlert: Bool = false
    @State private var showingErrorAlert: Bool = false
    @State private var showPolicy = false
    @State private var showAboutSheet = false
    @State private var showNotificationPermissionDeniedAlert = false
    @Environment(\.openURL) var openURL
    @EnvironmentObject private var appDelegate: AppDelegate

    init(presenter: SettingsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        SettingsScreen(
            title: String(\.label_settings_title),
            themeItem: themeItem,
            imageQualityItem: imageQualityItem,
            behaviorToggles: behaviorToggles,
            privacyToggles: privacyToggles,
            infoItems: infoItems,
            traktItems: traktItems,
            debugItems: debugItems,
            onBack: { presenter.dispatch(action: BackClicked__()) }
        )
        .settingsObservers(
            uiState: uiState,
            store: store,
            showingErrorAlert: $showingErrorAlert
        )
        .settingsAlerts(
            uiState: uiState,
            showingErrorAlert: $showingErrorAlert,
            showingLogoutAlert: $showingLogoutAlert,
            onLogout: { presenter.dispatch(action: TraktLogoutClicked()) }
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
        .sheet(isPresented: $showAboutSheet) {
            AboutSheet()
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
        }
    }

    // MARK: - Theme

    private var themeItem: SettingsThemeItem<DeviceAppTheme> {
        SettingsThemeItem(
            icon: "paintpalette",
            title: String(\.settings_theme_selector_title),
            subtitle: String(\.settings_theme_selector_subtitle),
            themes: DeviceAppTheme.sortedThemes,
            selectedTheme: store.appTheme,
            onThemeSelected: { selectedTheme in
                store.appTheme = selectedTheme
                let appTheme = selectedTheme.toAppTheme()
                presenter.dispatch(action: ThemeSelected(theme: appTheme.toThemeModel()))
            }
        )
    }

    // MARK: - Image Quality

    private var imageQualityItem: SettingsImageQualityItem {
        let currentQuality = uiState.imageQuality.toSwift()
        return SettingsImageQualityItem(
            icon: "photo",
            title: String(\.label_settings_image_quality),
            subtitle: imageQualityDescription(for: currentQuality),
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

    // MARK: - Behavior Toggles

    private var behaviorToggles: [SettingsToggleItem] {
        var toggles: [SettingsToggleItem] = []

        toggles.append(SettingsToggleItem(
            id: "notifications",
            icon: "bell.fill",
            title: String(\.label_settings_episode_notifications),
            subtitle: String(\.label_settings_episode_notifications_description),
            isOn: uiState.episodeNotificationsEnabled,
            onToggle: { handleNotificationToggle(enabled: $0) }
        ))

        var syncSubtitle: String?
        if uiState.showLastSyncDate, let lastSyncDate = uiState.lastSyncDate {
            syncSubtitle = String(\.label_settings_last_sync_date, parameter: lastSyncDate)
        }
        toggles.append(SettingsToggleItem(
            id: "sync",
            icon: "arrow.triangle.2.circlepath",
            title: String(\.label_settings_sync_update),
            subtitle: String(\.label_settings_sync_update_description),
            secondarySubtitle: syncSubtitle,
            isOn: uiState.backgroundSyncEnabled,
            onToggle: { presenter.dispatch(action: BackgroundSyncToggled(enabled: $0)) }
        ))

        toggles.append(SettingsToggleItem(
            id: "specials",
            icon: "film.stack",
            title: String(\.label_settings_include_specials),
            subtitle: String(\.label_settings_include_specials_description),
            isOn: uiState.includeSpecials,
            onToggle: { presenter.dispatch(action: IncludeSpecialsToggled(enabled: $0)) }
        ))

        toggles.append(SettingsToggleItem(
            id: "youtube",
            icon: "tv",
            title: String(\.label_settings_youtube),
            subtitle: String(\.label_settings_youtube_description),
            isOn: uiState.openTrailersInYoutube,
            onToggle: { presenter.dispatch(action: YoutubeToggled(enabled: $0)) }
        ))

        return toggles
    }

    // MARK: - Privacy Toggles

    private var privacyToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "crash-reporting",
                icon: "ladybug",
                title: String(\.label_settings_crash_reporting),
                subtitle: String(\.label_settings_crash_reporting_description),
                isOn: uiState.crashReportingEnabled,
                onToggle: { presenter.dispatch(action: CrashReportingToggled(enabled: $0)) }
            ),
        ]
    }

    // MARK: - Info Items

    private var infoItems: [SettingsNavigationItem] {
        [
            SettingsNavigationItem(
                id: "about",
                icon: "info.circle",
                title: String(\.settings_about_section_title),
                subtitle: String(\.settings_title_about),
                onTap: { showAboutSheet = true }
            ),
            SettingsNavigationItem(
                id: "privacy",
                icon: "hand.raised",
                title: String(\.label_settings_privacy_policy),
                onTap: { showPolicy = true }
            ),
        ]
    }

    // MARK: - Trakt Items

    private var traktItems: [SettingsNavigationItem] {
        guard uiState.isAuthenticated else { return [] }
        return [
            SettingsNavigationItem(
                id: "logout",
                icon: "person.fill",
                title: String(\.logout),
                subtitle: String(\.trakt_description),
                onTap: { showingLogoutAlert = true }
            ),
        ]
    }

    // MARK: - Debug Items

    private var debugItems: [SettingsNavigationItem] {
        #if DEBUG
            return [
                SettingsNavigationItem(
                    id: "debug",
                    icon: "ellipsis.curlybraces",
                    title: String(\.label_debug_menu_title),
                    subtitle: String(\.label_debug_menu_subtitle),
                    onTap: { presenter.dispatch(action: NavigateToDebugMenu()) }
                ),
            ]
        #else
            return []
        #endif
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

    // MARK: - About Sheet

    private func AboutSheet() -> some View {
        ZStack(alignment: .bottom) {
            ScrollView {
                VStack(spacing: 0) {
                    VStack(spacing: 16) {
                        Image(AppIcon.tvManiacIcon)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 72, height: 72)
                            .clipShape(RoundedRectangle(cornerRadius: 16))

                        Text("TvManiac")
                            .font(.title)
                            .bold()

                        Text(String(\.settings_about_version, parameter: uiState.versionName))
                            .font(.body)
                    }
                    .padding(.vertical, 32)

                    Divider()

                    VStack(alignment: .leading, spacing: 8) {
                        Text(String(\.settings_about_section_title))
                            .font(.headline)
                        Text(String(\.settings_about_description))
                            .font(.body)
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(16)

                    Divider()

                    Button {
                        if let url = URL(string: uiState.githubUrl) {
                            openURL(url)
                        }
                    } label: {
                        HStack {
                            Text(String(\.settings_about_source_code))
                            Spacer()
                            Text(String(\.settings_about_github))
                                .foregroundColor(.accentColor)
                        }
                        .padding(16)
                    }
                    .buttonStyle(.plain)

                    Divider()

                    Spacer()
                        .frame(height: 80)
                }
            }

            Text(String(\.settings_about_api_disclaimer))
                .font(.caption)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .padding(24)
                .frame(maxWidth: .infinity)
                .background(Color(.systemBackground))
        }
        .frame(maxWidth: .infinity)
        .presentationDetents([.large])
    }

    // MARK: - Helpers

    private func imageQualityTitle(for quality: SwiftImageQuality) -> String {
        switch quality {
        case .auto:
            String(\.label_settings_image_quality_auto)
        case .high:
            String(\.label_settings_image_quality_high)
        case .medium:
            String(\.label_settings_image_quality_medium)
        case .low:
            String(\.label_settings_image_quality_low)
        }
    }

    private func imageQualityDescription(for quality: SwiftImageQuality) -> String {
        switch quality {
        case .auto:
            String(\.label_settings_image_quality_auto_description)
        case .high:
            String(\.label_settings_image_quality_high_description)
        case .medium:
            String(\.label_settings_image_quality_medium_description)
        case .low:
            String(\.label_settings_image_quality_low_description)
        }
    }
}

private extension View {
    func settingsObservers(
        uiState: SettingsState,
        store: SettingsAppStorage,
        showingErrorAlert: Binding<Bool>
    ) -> some View {
        onChange(of: uiState.theme) { _, newTheme in
            store.appTheme = newTheme.toDeviceAppTheme()
        }
        .onChange(of: uiState.imageQuality) { _, imageQuality in
            store.imageQuality = imageQuality.toSwift()
        }
        .onChange(of: uiState.errorMessage) { _, errorMessage in
            showingErrorAlert.wrappedValue = errorMessage != nil
        }
    }

    func settingsAlerts(
        uiState: SettingsState,
        showingErrorAlert: Binding<Bool>,
        showingLogoutAlert: Binding<Bool>,
        onLogout: @escaping () -> Void
    ) -> some View {
        alert(isPresented: showingErrorAlert) {
            Alert(
                title: Text("Error"),
                message: Text(uiState.errorMessage ?? "An error occurred"),
                dismissButton: .default(Text("OK"))
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
    }
}
