import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct SettingsView: View {
    @Theme private var theme

    private let presenter: SettingsPresenter
    @StateObject @KotlinStateFlow private var uiState: SettingsState
    @StateObject private var store = SettingsAppStorage.shared
    @State private var showingLogoutAlert: Bool = false
    @State private var showingErrorAlert: Bool = false
    @State private var showPolicy = false
    @State private var showAboutSheet = false
    @Environment(\.openURL) var openURL
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject private var appDelegate: AppDelegate

    init(presenter: SettingsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                sectionHeader(String(\.label_settings_section_appearance))
                    .padding(.top, theme.spacing.medium)

                themeTitleSection
                    .padding(.top, theme.spacing.medium)

                imageQualitySection
                    .padding(.top, theme.spacing.large)

                sectionHeader(String(\.label_settings_section_behavior))
                    .padding(.top, theme.spacing.xLarge)

                youtubeToggleRow
                    .padding(.top, theme.spacing.medium)

                includeSpecialsToggleRow
                    .padding(.top, theme.spacing.medium)

                sectionHeader(String(\.settings_title_info))
                    .padding(.top, theme.spacing.xLarge)

                aboutRow
                    .padding(.top, theme.spacing.medium)

                privacyRow
                    .padding(.top, theme.spacing.xSmall)

                if uiState.isAuthenticated {
                    sectionHeader(String(\.settings_title_trakt))
                        .padding(.top, theme.spacing.xLarge)

                    traktLogoutRow
                        .padding(.top, theme.spacing.medium)
                }

                Spacer()
                    .frame(height: theme.spacing.xLarge)
            }
            .padding(.horizontal, theme.spacing.medium)
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
                        .foregroundColor(theme.colors.accent)
                }
            }
        }
        .toolbarBackground(theme.colors.surface, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
        .onChange(of: uiState.theme) { newTheme in
            store.appTheme = newTheme.toDeviceAppTheme()
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
        .alert(isPresented: $showingLogoutAlert) {
            Alert(
                title: Text(String(\.trakt_dialog_logout_title)),
                message: Text(String(\.trakt_dialog_logout_message)),
                primaryButton: .destructive(Text(String(\.logout))) {
                    presenter.dispatch(action: TraktLogoutClicked())
                },
                secondaryButton: .cancel()
            )
        }
        .sheet(isPresented: $showAboutSheet) {
            AboutSheet()
        }
        .sheet(isPresented: $showPolicy) {
            if let url = URL(string: "https://github.com/c0de-wizard/tv-maniac") {
                SFSafariViewWrapper(url: url)
                    .appTint()
                    .appTheme()
            }
        }
        .onAppear {
            store.imageQuality = uiState.imageQuality.toSwift()
        }
    }

    @ViewBuilder
    private func sectionHeader(
        _ title: String,
        icon: String? = nil,
        subtitle: String? = nil
    ) -> some View {
        HStack(spacing: theme.spacing.medium) {
            if let icon {
                Image(systemName: icon)
                    .foregroundColor(theme.colors.secondary)
                    .frame(width: theme.spacing.large, height: theme.spacing.large)
            }
            VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                Text(title)
                    .textStyle(theme.typography.titleMedium)
                    .foregroundColor(theme.colors.onSurface)
                if let subtitle {
                    Text(subtitle)
                        .textStyle(theme.typography.bodySmall)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }
            }
        }
    }

    @ViewBuilder
    private var themeTitleSection: some View {
        VStack(alignment: .leading, spacing: theme.spacing.small) {
            HStack(spacing: theme.spacing.medium) {
                settingsIcon("paintpalette", color: theme.colors.secondary)

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(String(\.settings_theme_selector_title))
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                    Text(String(\.settings_theme_selector_subtitle))
                        .textStyle(theme.typography.bodySmall)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }
            }

            ThemeSelectorView(
                themes: DeviceAppTheme.sortedThemes,
                selectedTheme: store.appTheme,
                onThemeSelected: { selectedTheme in
                    store.appTheme = selectedTheme
                    let appTheme = selectedTheme.toAppTheme()
                    presenter.dispatch(action: ThemeSelected(theme: appTheme.toThemeModel()))
                }
            )
        }
    }

    @ViewBuilder
    private var imageQualitySection: some View {
        VStack(alignment: .leading, spacing: theme.spacing.small) {
            HStack(spacing: theme.spacing.medium) {
                settingsIcon("photo", color: theme.colors.secondary)

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(String(\.label_settings_image_quality))
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                    Text(imageQualityDescription(for: uiState.imageQuality.toSwift()))
                        .textStyle(theme.typography.bodySmall)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }
            }

            HStack(spacing: theme.spacing.small) {
                ForEach(SwiftImageQuality.allCases, id: \.self) { quality in
                    SelectionChip(
                        label: imageQualityTitle(for: quality),
                        isSelected: uiState.imageQuality.toSwift() == quality,
                        action: {
                            let kmpQuality = TvManiac.ImageQuality.fromSwift(quality)
                            presenter.dispatch(action: ImageQualitySelected(quality: kmpQuality))
                            store.imageQuality = quality
                        }
                    )
                }
            }
            .padding(.leading, 40)
        }
    }

    @ViewBuilder
    private var youtubeToggleRow: some View {
        HStack(spacing: theme.spacing.medium) {
            settingsIcon("tv", color: theme.colors.secondary)

            VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                Text(String(\.label_settings_youtube))
                    .textStyle(theme.typography.titleMedium)
                    .foregroundColor(theme.colors.onSurface)
                Text(String(\.label_settings_youtube_description))
                    .textStyle(theme.typography.bodySmall)
                    .foregroundColor(theme.colors.onSurfaceVariant)
            }

            Spacer()

            Toggle("", isOn: Binding(
                get: { uiState.openTrailersInYoutube },
                set: { newValue in
                    presenter.dispatch(action: YoutubeToggled(enabled: newValue))
                }
            ))
            .labelsHidden()
            .tint(theme.colors.secondary)
        }
    }

    @ViewBuilder
    private var includeSpecialsToggleRow: some View {
        HStack(spacing: theme.spacing.medium) {
            settingsIcon("film.stack", color: theme.colors.secondary)

            VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                Text(String(\.label_settings_include_specials))
                    .textStyle(theme.typography.titleMedium)
                    .foregroundColor(theme.colors.onSurface)
                Text(String(\.label_settings_include_specials_description))
                    .textStyle(theme.typography.bodySmall)
                    .foregroundColor(theme.colors.onSurfaceVariant)
            }

            Spacer()

            Toggle("", isOn: Binding(
                get: { uiState.includeSpecials },
                set: { newValue in
                    presenter.dispatch(action: IncludeSpecialsToggled(enabled: newValue))
                }
            ))
            .labelsHidden()
            .tint(theme.colors.secondary)
        }
    }

    @ViewBuilder
    private var aboutRow: some View {
        Button {
            showAboutSheet = true
        } label: {
            HStack(spacing: theme.spacing.medium) {
                settingsIcon("info.circle", color: theme.colors.secondary)

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(String(\.settings_about_section_title))
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                    Text(String(\.settings_title_about))
                        .textStyle(theme.typography.bodySmall)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .foregroundColor(theme.colors.onSurfaceVariant)
            }
            .padding(.vertical, theme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    private var privacyRow: some View {
        Button {
            showPolicy = true
        } label: {
            HStack(spacing: theme.spacing.medium) {
                settingsIcon("hand.raised", color: theme.colors.secondary)

                Text(String(\.label_settings_privacy_policy))
                    .textStyle(theme.typography.titleMedium)
                    .foregroundColor(theme.colors.onSurface)

                Spacer()

                Image(systemName: "chevron.right")
                    .foregroundColor(theme.colors.onSurfaceVariant)
            }
            .padding(.vertical, theme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    private var traktLogoutRow: some View {
        Button {
            showingLogoutAlert = true
        } label: {
            HStack(spacing: theme.spacing.medium) {
                settingsIcon("person.fill", color: theme.colors.secondary)

                VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                    Text(String(\.logout))
                        .textStyle(theme.typography.titleMedium)
                        .foregroundColor(theme.colors.onSurface)
                    Text(String(\.trakt_description))
                        .textStyle(theme.typography.bodySmall)
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .foregroundColor(theme.colors.onSurfaceVariant)
            }
            .padding(.vertical, theme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    private func settingsIcon(_ systemName: String, color: Color) -> some View {
        Image(systemName: systemName)
            .foregroundColor(color)
            .frame(width: theme.spacing.large, height: theme.spacing.large)
    }

    @ViewBuilder
    private func AboutSheet() -> some View {
        ZStack(alignment: .bottom) {
            ScrollView {
                VStack(spacing: 0) {
                    VStack(spacing: theme.spacing.medium) {
                        Image("TvManiacIcon")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 72, height: 72)
                            .clipShape(RoundedRectangle(cornerRadius: 16))

                        Text("TvManiac")
                            .textStyle(theme.typography.headlineLarge)
                            .foregroundColor(theme.colors.onSurface)

                        Text("Version 1.0.0")
                            .textStyle(theme.typography.bodyLarge)
                            .foregroundColor(theme.colors.secondary)
                    }
                    .padding(.vertical, theme.spacing.xLarge)

                    Divider()
                        .overlay(theme.colors.outline)

                    VStack(alignment: .leading, spacing: theme.spacing.xSmall) {
                        Text(String(\.settings_about_section_title))
                            .textStyle(theme.typography.titleMedium)
                            .foregroundColor(theme.colors.onSurface)

                        Text(String(\.settings_about_description))
                            .textStyle(theme.typography.bodyMedium)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal, theme.spacing.medium)
                    .padding(.vertical, theme.spacing.medium)

                    Divider()
                        .overlay(theme.colors.outline)

                    Button {
                        if let url = URL(string: "https://github.com/c0de-wizard/tv-maniac") {
                            openURL(url)
                        }
                    } label: {
                        HStack {
                            Text(String(\.settings_about_source_code))
                                .textStyle(theme.typography.bodyLarge)
                                .foregroundColor(theme.colors.onSurface)

                            Spacer()

                            Text(String(\.settings_about_github))
                                .textStyle(theme.typography.bodyLarge)
                                .foregroundColor(theme.colors.secondary)
                        }
                        .padding(.horizontal, theme.spacing.medium)
                        .padding(.vertical, theme.spacing.medium)
                    }
                    .buttonStyle(.plain)

                    Divider()
                        .overlay(theme.colors.outline)

                    Spacer()
                        .frame(height: 80)
                }
            }

            Text(String(\.settings_about_api_disclaimer))
                .textStyle(theme.typography.bodySmall)
                .foregroundColor(theme.colors.onSurfaceVariant)
                .multilineTextAlignment(.center)
                .padding(.horizontal, theme.spacing.large)
                .padding(.vertical, theme.spacing.large)
                .frame(maxWidth: .infinity)
                .background(theme.colors.surface)
        }
        .frame(maxWidth: .infinity)
        .background(theme.colors.surface)
        .presentationDetents([.large])
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
