import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct SettingsTab: View {
    private let presenter: SettingsPresenter
    @StateObject @KotlinStateFlow private var uiState: SettingsState
    @StateObject private var store = SettingsAppStorage.shared
    @State private var showingAlert: Bool = false
    @State private var openInYouTube: Bool = false
    @State private var showPolicy = false
    @State private var aboutPage = false
    @Environment(\.openURL) var openURL
    @Environment(\.presentationMode) var presentationMode
    @ObservedObject private var model = TraktAuthViewModel()

    init(presenter: SettingsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
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
            Button {
                showingAlert = !(uiState.showTraktDialog)
            } label: {
                settingsLabel(
                    title: String(\.label_settings_trakt_connect),
                    icon: "person.fill",
                    color: Color.accent
                )
            }
            .buttonStyle(.plain)
            .alert(isPresented: $showingAlert) {
                Alert(
                    title: Text(String(\.label_settings_trakt_dialog_title)),
                    message: Text(String(\.label_settings_trakt_dialog_message)),
                    primaryButton: .default(Text(String(\.label_settings_trakt_dialog_button_primary))) {
                        model.initiateAuthorization()
                    },
                    secondaryButton: .destructive(Text(String(\.label_settings_trakt_dialog_button_secondary)))
                )
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
