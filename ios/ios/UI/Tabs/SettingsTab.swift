import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct SettingsTab: View {
    private let presenter: SettingsPresenter
    @StateObject @KotlinStateFlow private var uiState: SettingsState
    @StateObject private var store = SettingsAppStorage.shared
    @State private var showingAlert: Bool = false
    @State private var showingErrorAlert: Bool = false
    @State private var openInYouTube: Bool = false
    @State private var showPolicy = false
    @State private var aboutPage = false
    @Environment(\.openURL) var openURL
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject private var appDelegate: AppDelegate
    private let authCoordinator: TraktAuthCoordinator

    init(presenter: SettingsPresenter, authRepository: TraktAuthRepository, logger: Logger) {
        self.presenter = presenter
        _uiState = .init(presenter.state)

        guard let redirectURL = URL(string: BuildConfig.shared.TRAKT_REDIRECT_URI) else {
            fatalError("Invalid Trakt redirect URI in BuildConfig")
        }

        authCoordinator = TraktAuthCoordinator(
            authRepository: authRepository,
            logger: logger,
            clientId: BuildConfig.shared.TRAKT_CLIENT_ID,
            clientSecret: BuildConfig.shared.TRAKT_CLIENT_SECRET,
            redirectURL: redirectURL
        )
    }

    @ViewBuilder
    private var themeSection: some View {
        Section(String(\.label_settings_section_app_theme)) {
            themePickerView

            Text(String(\.settings_theme_description))
                .font(.caption)
                .foregroundColor(.secondary)
                .padding(.horizontal)
        }
    }

    @ViewBuilder
    private var themePickerView: some View {
        Picker(
            selection: Binding(
                get: { store.appTheme },
                set: { theme in
                    store.appTheme = theme
                    let appTheme: AppTheme = switch theme {
                    case .light:
                        .lightTheme
                    case .dark:
                        .darkTheme
                    case .system:
                        .systemTheme
                    }
                    presenter.dispatch(action: ThemeSelected(appTheme: appTheme))
                }
            ),
            label: settingsLabel(
                title: String(\.label_settings_change_theme),
                icon: "paintpalette",
                color: .blue
            )
        ) {
            ForEach(DeveiceAppTheme.allCases, id: \.self) { theme in
                Text(themeDropdownTitle(for: theme))
                    .tag(theme)
            }
        }
        .pickerStyle(.menu)
    }

    @ViewBuilder
    private var behaviorSection: some View {
        Section(String(\.label_settings_section_behavior)) {
            youtubeToggle
            imageQualityPicker
            imageQualityDescription
        }
    }

    @ViewBuilder
    private var youtubeToggle: some View {
        Toggle(isOn: $openInYouTube) {
            settingsLabel(
                title: String(\.label_settings_youtube),
                icon: "tv",
                color: .red
            )
        }
    }

    @ViewBuilder
    private var imageQualityPicker: some View {
        Picker(
            selection: Binding(
                get: { uiState.imageQuality.toSwift() },
                set: { swiftQuality in
                    let quality = TvManiac.ImageQuality.fromSwift(swiftQuality)
                    presenter.dispatch(action: ImageQualitySelected(quality: quality))
                    store.imageQuality = swiftQuality
                }
            ),
            label: settingsLabel(
                title: String(\.label_settings_image_quality),
                icon: "photo",
                color: .blue
            )
        ) {
            ForEach(SwiftImageQuality.allCases, id: \.self) { quality in
                Text(imageQualityTitle(for: quality))
                    .tag(quality)
            }
        }
        .pickerStyle(.menu)
    }

    @ViewBuilder
    private var imageQualityDescription: some View {
        Text(imageQualityDescription(for: uiState.imageQuality.toSwift()))
            .font(.caption)
            .foregroundColor(.secondary)
            .padding(.horizontal)
    }

    @ViewBuilder
    private var traktSection: some View {
        Section(String(\.label_settings_section_trakt_account)) {
            HStack {
                Button {
                    showingAlert = true
                } label: {
                    settingsLabel(
                        title: uiState.isAuthenticated ? String(\.logout) : String(\.label_settings_trakt_connect),
                        icon: "person.fill",
                        color: Color.accent
                    )
                }
                .buttonStyle(.plain)

                if uiState.isLoading {
                    Spacer()
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                }
            }
            .alert(isPresented: $showingAlert) {
                if uiState.isAuthenticated {
                    Alert(
                        title: Text(String(\.trakt_dialog_logout_title)),
                        message: Text(String(\.trakt_dialog_logout_message)),
                        primaryButton: .destructive(Text(String(\.logout))) {
                            presenter.dispatch(action: TraktLogoutClicked())
                        },
                        secondaryButton: .cancel()
                    )
                } else {
                    Alert(
                        title: Text(String(\.trakt_dialog_login_title)),
                        message: Text(String(\.trakt_dialog_login_message)),
                        primaryButton: .default(Text(String(\.login))) {
                            authCoordinator.initiateAuthorization()
                        },
                        secondaryButton: .cancel()
                    )
                }
            }
        }
    }

    @ViewBuilder
    private var infoSection: some View {
        Section(String(\.label_settings_section_info)) {
            aboutButton
            privacyButton
        }
    }

    @ViewBuilder
    private var aboutButton: some View {
        Button {
            aboutPage.toggle()
        } label: {
            settingsLabel(
                title: String(\.label_settings_about),
                icon: "info.circle",
                color: .black
            )
        }
        .buttonStyle(.plain)
        .sheet(isPresented: $aboutPage) {
            if let url = URL(string: "https://github.com/c0de-wizard/tv-maniac") {
                SFSafariViewWrapper(url: url)
                    .appTint()
                    .appTheme()
            }
        }
    }

    @ViewBuilder
    private var privacyButton: some View {
        Button {
            showPolicy.toggle()
        } label: {
            settingsLabel(
                title: String(\.label_settings_privacy_policy),
                icon: "hand.raised",
                color: .indigo
            )
        }
        .buttonStyle(.plain)
        .sheet(isPresented: $showPolicy) {
            // TODO: Add Privacy Policy
            if let url = URL(string: "https://github.com/c0de-wizard/tv-maniac") {
                SFSafariViewWrapper(url: url)
                    .appTint()
                    .appTheme()
            }
        }
    }

    var body: some View {
        Form {
            themeSection
            behaviorSection
            traktSection
            infoSection
        }
        .scrollContentBackground(.hidden)
        .background(Color.backgroundColor)
        .navigationTitle(Text(String(\.label_settings_title)))
        .navigationBarTitleDisplayMode(.inline)
        .onChange(of: uiState.appTheme) { newTheme in
            store.appTheme = newTheme.toDeveiceAppTheme()
        }
        .onChange(of: uiState.imageQuality) { imageQuality in
            store.imageQuality = imageQuality.toSwift()
        }
        .onChange(of: uiState.errorMessage) { errorMessage in
            showingErrorAlert = errorMessage != nil
        }
        .alert(isPresented: $showingErrorAlert) {
            Alert(
                title: Text("Error"),
                message: Text(uiState.errorMessage ?? "An error occurred"),
                dismissButton: .default(Text("OK"))
            )
        }
        .onAppear {
            store.imageQuality = uiState.imageQuality.toSwift()
        }
    }

    private func settingsLabel(title: String, icon: String, color: Color) -> some View {
        HStack {
            ZStack {
                Rectangle()
                    .fill(color)
                    .clipShape(RoundedRectangle(cornerRadius: 6, style: .continuous))
                Image(systemName: icon)
                    .foregroundColor(.white)
            }
            .frame(width: 30, height: 30, alignment: .center)
            .padding(.trailing, 8)
            .accessibilityHidden(true)

            Text(title)
        }
        .padding(.vertical, 2)
    }

    private func themeTitle(for theme: DeveiceAppTheme) -> String {
        switch theme {
        case .light:
            String(\.settings_title_theme_light)
        case .dark:
            String(\.settings_title_theme_dark)
        case .system:
            String(\.settings_title_theme_system)
        }
    }

    private func themeDropdownTitle(for theme: DeveiceAppTheme) -> String {
        switch theme {
        case .light:
            String(\.settings_theme_light)
        case .dark:
            String(\.settings_theme_dark)
        case .system:
            String(\.settings_theme_system)
        }
    }

    private func imageQualityTitle(for quality: SwiftImageQuality) -> String {
        switch quality {
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
        case .high:
            String(\.label_settings_image_quality_high_description)
        case .medium:
            String(\.label_settings_image_quality_medium_description)
        case .low:
            String(\.label_settings_image_quality_low_description)
        }
    }
}
