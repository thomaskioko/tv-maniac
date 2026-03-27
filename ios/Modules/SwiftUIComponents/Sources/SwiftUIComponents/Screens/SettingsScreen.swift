import SwiftUI

public struct SettingsScreen<Theme: ThemeItem>: View {
    @SwiftUIComponents.Theme private var appTheme

    private let title: String
    private let themeItem: SettingsThemeItem<Theme>
    private let imageQualityItem: SettingsImageQualityItem
    private let behaviorToggles: [SettingsToggleItem]
    private let privacyToggles: [SettingsToggleItem]
    private let infoItems: [SettingsNavigationItem]
    private let traktItems: [SettingsNavigationItem]
    private let onBack: () -> Void

    public init(
        title: String,
        themeItem: SettingsThemeItem<Theme>,
        imageQualityItem: SettingsImageQualityItem,
        behaviorToggles: [SettingsToggleItem],
        privacyToggles: [SettingsToggleItem],
        infoItems: [SettingsNavigationItem],
        traktItems: [SettingsNavigationItem] = [],
        onBack: @escaping () -> Void
    ) {
        self.title = title
        self.themeItem = themeItem
        self.imageQualityItem = imageQualityItem
        self.behaviorToggles = behaviorToggles
        self.privacyToggles = privacyToggles
        self.infoItems = infoItems
        self.traktItems = traktItems
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

                if !behaviorToggles.isEmpty {
                    sectionHeader("Behavior")
                        .padding(.top, appTheme.spacing.xLarge)

                    ForEach(behaviorToggles) { toggle in
                        toggleRow(toggle)
                            .padding(.top, appTheme.spacing.medium)
                    }
                }

                if !privacyToggles.isEmpty {
                    sectionHeader("Privacy")
                        .padding(.top, appTheme.spacing.xLarge)

                    ForEach(privacyToggles) { toggle in
                        toggleRow(toggle)
                            .padding(.top, appTheme.spacing.medium)
                    }
                }

                if !infoItems.isEmpty {
                    sectionHeader("Info")
                        .padding(.top, appTheme.spacing.xLarge)

                    ForEach(Array(infoItems.enumerated()), id: \.element.id) { index, item in
                        navigationRow(item)
                            .padding(.top, index == 0 ? appTheme.spacing.medium : appTheme.spacing.xSmall)
                    }
                }

                if !traktItems.isEmpty {
                    sectionHeader("Trakt")
                        .padding(.top, appTheme.spacing.xLarge)

                    ForEach(traktItems) { item in
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
                title: title,
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
                settingsIcon(themeItem.icon, color: appTheme.colors.secondary)

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(themeItem.title)
                        .textStyle(appTheme.typography.titleMedium)
                        .foregroundColor(appTheme.colors.onSurface)
                    Text(themeItem.subtitle)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }
            }

            ThemeSelectorView(
                themes: themeItem.themes,
                selectedTheme: themeItem.selectedTheme,
                onThemeSelected: themeItem.onThemeSelected
            )
        }
    }

    private var imageQualitySectionContent: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.small) {
            HStack(spacing: appTheme.spacing.medium) {
                settingsIcon(imageQualityItem.icon, color: appTheme.colors.secondary)

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(imageQualityItem.title)
                        .textStyle(appTheme.typography.titleMedium)
                        .foregroundColor(appTheme.colors.onSurface)
                    Text(imageQualityItem.subtitle)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }
            }

            HStack(spacing: appTheme.spacing.small) {
                ForEach(imageQualityItem.options) { option in
                    SelectionChip(
                        label: option.label,
                        isSelected: option.id == imageQualityItem.selectedOptionId,
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
