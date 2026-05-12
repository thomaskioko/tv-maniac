import SwiftUI

public struct SettingsScreen<Theme: ThemeItem>: View {
    public struct State {
        public let title: String
        public let themeItem: SettingsThemeItem<Theme>
        public let imageQualityItem: SettingsImageQualityItem
        public let behaviorToggles: [SettingsToggleItem]
        public let privacyToggles: [SettingsToggleItem]
        public let infoItems: [SettingsNavigationItem]
        public let traktItems: [SettingsNavigationItem]

        public init(
            title: String,
            themeItem: SettingsThemeItem<Theme>,
            imageQualityItem: SettingsImageQualityItem,
            behaviorToggles: [SettingsToggleItem],
            privacyToggles: [SettingsToggleItem],
            infoItems: [SettingsNavigationItem],
            traktItems: [SettingsNavigationItem] = []
        ) {
            self.title = title
            self.themeItem = themeItem
            self.imageQualityItem = imageQualityItem
            self.behaviorToggles = behaviorToggles
            self.privacyToggles = privacyToggles
            self.infoItems = infoItems
            self.traktItems = traktItems
        }
    }

    @SwiftUIComponents.Theme private var appTheme

    private let state: State
    private let onBack: () -> Void

    public init(
        state: State,
        onBack: @escaping () -> Void
    ) {
        self.state = state
        self.onBack = onBack
    }

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                sectionHeader("Appearance")
                    .padding(.top, appTheme.spacing.medium)

                themeSectionContent
                    .padding(.top, appTheme.spacing.medium)

                imageQualitySectionContent
                    .padding(.top, appTheme.spacing.large)

                if !state.behaviorToggles.isEmpty {
                    sectionHeader("Behavior")
                        .padding(.top, appTheme.spacing.xLarge)

                    ForEach(state.behaviorToggles) { toggle in
                        toggleRow(toggle)
                            .padding(.top, appTheme.spacing.medium)
                    }
                }

                if !state.privacyToggles.isEmpty {
                    sectionHeader("Privacy")
                        .padding(.top, appTheme.spacing.xLarge)

                    ForEach(state.privacyToggles) { toggle in
                        toggleRow(toggle)
                            .padding(.top, appTheme.spacing.medium)
                    }
                }

                if !state.infoItems.isEmpty {
                    sectionHeader("Info")
                        .padding(.top, appTheme.spacing.xLarge)

                    ForEach(Array(state.infoItems.enumerated()), id: \.element.id) { index, item in
                        navigationRow(item)
                            .padding(.top, index == 0 ? appTheme.spacing.medium : appTheme.spacing.xSmall)
                    }
                }

                if !state.traktItems.isEmpty {
                    sectionHeader("Trakt")
                        .padding(.top, appTheme.spacing.xLarge)

                    ForEach(state.traktItems) { item in
                        navigationRow(item)
                            .padding(.top, appTheme.spacing.medium)
                    }
                }

                Spacer()
                    .frame(height: appTheme.spacing.xLarge)
            }
            .padding(.horizontal, appTheme.spacing.medium)
            .padding(.top, toolbarInset)
        }
        .scrollContentBackground(.hidden)
        .background(appTheme.colors.background)
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .navigationBarColor(backgroundColor: .clear)
        .swipeBackGesture(onSwipe: onBack)
        .overlay(
            GlassToolbar(
                title: state.title,
                opacity: 1.0,
                leadingIcon: {
                    GlassButton(icon: "chevron.left", action: onBack)
                }
            ),
            alignment: .top
        )
        .edgesIgnoringSafeArea(.top)
    }

    private func sectionHeader(_ title: String) -> some View {
        Text(title)
            .textStyle(appTheme.typography.titleMedium)
            .foregroundColor(appTheme.colors.onSurface)
    }

    private var themeSectionContent: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.small) {
            HStack(spacing: appTheme.spacing.medium) {
                settingsIcon(state.themeItem.icon, color: appTheme.colors.secondary)

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(state.themeItem.title)
                        .textStyle(appTheme.typography.titleMedium)
                        .foregroundColor(appTheme.colors.onSurface)
                    Text(state.themeItem.subtitle)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }
            }

            ThemeSelectorView(
                themes: state.themeItem.themes,
                selectedTheme: state.themeItem.selectedTheme,
                onThemeSelected: state.themeItem.onThemeSelected
            )
        }
    }

    private var imageQualitySectionContent: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.small) {
            HStack(spacing: appTheme.spacing.medium) {
                settingsIcon(state.imageQualityItem.icon, color: appTheme.colors.secondary)

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(state.imageQualityItem.title)
                        .textStyle(appTheme.typography.titleMedium)
                        .foregroundColor(appTheme.colors.onSurface)
                    Text(state.imageQualityItem.subtitle)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }
            }

            HStack(spacing: appTheme.spacing.small) {
                ForEach(state.imageQualityItem.options) { option in
                    SelectionChip(
                        label: option.label,
                        isSelected: option.id == state.imageQualityItem.selectedOptionId,
                        action: option.onSelect
                    )
                }
            }
            .padding(.leading, 40)
        }
    }

    private func toggleRow(_ item: SettingsToggleItem) -> some View {
        HStack(spacing: appTheme.spacing.medium) {
            settingsIcon(item.icon, color: appTheme.colors.secondary)

            VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                Text(item.title)
                    .textStyle(appTheme.typography.titleMedium)
                    .foregroundColor(appTheme.colors.onSurface)
                Text(item.subtitle)
                    .textStyle(appTheme.typography.bodySmall)
                    .foregroundColor(appTheme.colors.onSurfaceVariant)
                if let secondarySubtitle = item.secondarySubtitle {
                    Text(secondarySubtitle)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }
            }

            Spacer()

            Toggle("", isOn: Binding(
                get: { item.isOn },
                set: { item.onToggle($0) }
            ))
            .labelsHidden()
            .tint(appTheme.colors.secondary)
        }
    }

    private func navigationRow(_ item: SettingsNavigationItem) -> some View {
        Button {
            item.onTap()
        } label: {
            HStack(spacing: appTheme.spacing.medium) {
                settingsIcon(item.icon, color: appTheme.colors.secondary)

                if let subtitle = item.subtitle {
                    VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                        Text(item.title)
                            .textStyle(appTheme.typography.titleMedium)
                            .foregroundColor(appTheme.colors.onSurface)
                        Text(subtitle)
                            .textStyle(appTheme.typography.bodySmall)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                    }
                } else {
                    Text(item.title)
                        .textStyle(appTheme.typography.titleMedium)
                        .foregroundColor(appTheme.colors.onSurface)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .foregroundColor(appTheme.colors.onSurfaceVariant)
            }
            .padding(.vertical, appTheme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    private func settingsIcon(_ systemName: String, color: Color) -> some View {
        Image(systemName: systemName)
            .foregroundColor(color)
            .frame(width: appTheme.spacing.large, height: appTheme.spacing.large)
    }

    private var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        return 44 + safeAreaTop
    }
}
