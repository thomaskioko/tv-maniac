import SwiftUI
import SwiftUIComponents
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

    var body: some View {
        Form {
            Section(String(\.label_settings_section_app_theme)) {
                Picker(
                    selection: $store.appTheme,
                    label: Text(String(\.label_settings_change_theme))
                        .bodyMediumFont(size: 16),
                    content: {
                        ForEach(DeveiceAppTheme.allCases, id: \.self) { theme in
                            Text(theme.localizableName)
                                .tag(theme)
                        }
                    }
                )
                .pickerStyle(.segmented)
                .padding(.vertical, 6)
                .onChange(of: uiState.appTheme) { theme in
                    presenter.dispatch(action: ThemeSelected(appTheme: theme))
                }
            }

            Section(String(\.label_settings_section_behavior)) {
                Toggle(isOn: $openInYouTube) {
                    settingsLabel(
                        title: String(\.label_settings_youtube),
                        icon: "tv",
                        color: .red
                    )
                }
            }

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

            Section(String(\.label_settings_section_info)) {
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
        }
        .scrollContentBackground(.hidden)
        .background(Color.backgroundColor)
        .navigationTitle(Text(String(\.label_settings_title)))
        .navigationBarTitleDisplayMode(.inline)
        .onChange(of: uiState.appTheme) { newTheme in
            store.appTheme = newTheme.toDeveiceAppTheme()
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
}
