import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct SettingsView: View {
    @Theme private var theme

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

    init(presenter: SettingsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    @ViewBuilder
    private var themeSection: some View {
        Section(String(\.label_settings_section_app_theme)) {
            themePickerView

            Text(String(\.settings_theme_description))
                .textStyle(theme.typography.labelSmall)
                .foregroundColor(theme.colors.onSurfaceVariant)
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
                color: theme.colors.accent
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
                color: theme.colors.error
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
                color: theme.colors.accent
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
            .textStyle(theme.typography.labelSmall)
            .foregroundColor(theme.colors.onSurfaceVariant)
            .padding(.horizontal)
    }

    @ViewBuilder
    private var traktSection: some View {
        if uiState.isAuthenticated {
            Section(String(\.label_settings_section_trakt_account)) {
                HStack {
                    Button {
                        showingAlert = true
                    } label: {
                        HStack {
                            ZStack {
                                Rectangle()
                                    .fill(theme.colors.error)
                                    .clipShape(RoundedRectangle(cornerRadius: 6, style: .continuous))
                                Image(systemName: "person.fill")
                                    .foregroundColor(theme.colors.onError)
                            }
                            .frame(width: 30, height: 30, alignment: .center)
                            .padding(.trailing, 8)
                            .accessibilityHidden(true)

                            Text(String(\.logout))
                                .foregroundColor(theme.colors.error)
                        }
                        .padding(.vertical, 2)
                    }
                    .buttonStyle(.plain)
                }
                .alert(isPresented: $showingAlert) {
                    Alert(
                        title: Text(String(\.trakt_dialog_logout_title)),
                        message: Text(String(\.trakt_dialog_logout_message)),
                        primaryButton: .destructive(Text(String(\.logout))) {
                            presenter.dispatch(action: TraktLogoutClicked())
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
                color: theme.colors.onSurface
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
                color: theme.colors.secondary
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
            infoSection
            traktSection
        }
        .scrollContentBackground(.hidden)
        .background(theme.colors.background)
        .navigationTitle(Text(String(\.label_settings_title)))
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .swipeBackGesture {
            presenter.dispatch(action: BackClicked())
        }
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button {
                    presenter.dispatch(action: BackClicked())
                } label: {
                    Image(systemName: "chevron.left")
                        .textStyle(theme.typography.titleMedium)
                        .fontWeight(.semibold)
                        .foregroundColor(theme.colors.accent)
                }
            }
        }
        .toolbarBackground(theme.colors.background, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
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
                    .foregroundColor(theme.colors.onPrimary)
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
