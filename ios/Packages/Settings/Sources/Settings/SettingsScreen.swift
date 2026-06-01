import Components
import DesignSystem
import Models
import SwiftUI

public struct SettingsInfoContent {
    public let icon: Image
    public let appName: String
    public let versionText: String
    public let description: String
    public let sourceCodeLabel: String
    public let sourceCodeValue: String
    public let apiDisclaimer: String
    public let onVersionTap: () -> Void
    public let onSourceCodeTap: () -> Void

    public init(
        icon: Image,
        appName: String,
        versionText: String,
        description: String,
        sourceCodeLabel: String,
        sourceCodeValue: String,
        apiDisclaimer: String,
        onVersionTap: @escaping () -> Void,
        onSourceCodeTap: @escaping () -> Void
    ) {
        self.icon = icon
        self.appName = appName
        self.versionText = versionText
        self.description = description
        self.sourceCodeLabel = sourceCodeLabel
        self.sourceCodeValue = sourceCodeValue
        self.apiDisclaimer = apiDisclaimer
        self.onVersionTap = onVersionTap
        self.onSourceCodeTap = onSourceCodeTap
    }
}

public struct SettingsLicenseSection: Identifiable {
    public let id: String
    public let label: String
    public let items: [SettingsLinkItem]

    public init(id: String, label: String, items: [SettingsLinkItem]) {
        self.id = id
        self.label = label
        self.items = items
    }
}

public struct SettingsTraktContent {
    public let title: String
    public let description: String
    public let authenticationLabel: String
    public let connectedTitle: String
    public let connectedDescription: String
    public let isAuthenticated: Bool
    public let logoutLabel: String
    public let loginLabel: String
    public let onLogout: () -> Void
    public let onLogin: () -> Void

    public init(
        title: String,
        description: String,
        authenticationLabel: String,
        connectedTitle: String,
        connectedDescription: String,
        isAuthenticated: Bool,
        logoutLabel: String,
        loginLabel: String,
        onLogout: @escaping () -> Void,
        onLogin: @escaping () -> Void
    ) {
        self.title = title
        self.description = description
        self.authenticationLabel = authenticationLabel
        self.connectedTitle = connectedTitle
        self.connectedDescription = connectedDescription
        self.isAuthenticated = isAuthenticated
        self.logoutLabel = logoutLabel
        self.loginLabel = loginLabel
        self.onLogout = onLogout
        self.onLogin = onLogin
    }
}

public struct SettingsScreen<Theme: ThemeItem>: View {
    public struct State {
        public let rootTitle: String
        public let versionFooter: String
        public let currentPage: SettingsPageRoute
        public let rootSections: [SettingsRootSection]
        public let themeItem: SettingsThemeItem<Theme>
        public let imageQualityItem: SettingsImageQualityItem
        public let behaviorToggles: [SettingsToggleItem]
        public let notificationToggles: [SettingsToggleItem]
        public let privacyToggles: [SettingsToggleItem]
        public let privacyLinks: [SettingsNavigationItem]
        public let infoContent: SettingsInfoContent
        public let licenseSections: [SettingsLicenseSection]
        public let traktContent: SettingsTraktContent

        public init(
            rootTitle: String,
            versionFooter: String,
            currentPage: SettingsPageRoute,
            rootSections: [SettingsRootSection],
            themeItem: SettingsThemeItem<Theme>,
            imageQualityItem: SettingsImageQualityItem,
            behaviorToggles: [SettingsToggleItem],
            notificationToggles: [SettingsToggleItem],
            privacyToggles: [SettingsToggleItem],
            privacyLinks: [SettingsNavigationItem],
            infoContent: SettingsInfoContent,
            licenseSections: [SettingsLicenseSection],
            traktContent: SettingsTraktContent
        ) {
            self.rootTitle = rootTitle
            self.versionFooter = versionFooter
            self.currentPage = currentPage
            self.rootSections = rootSections
            self.themeItem = themeItem
            self.imageQualityItem = imageQualityItem
            self.behaviorToggles = behaviorToggles
            self.notificationToggles = notificationToggles
            self.privacyToggles = privacyToggles
            self.privacyLinks = privacyLinks
            self.infoContent = infoContent
            self.licenseSections = licenseSections
            self.traktContent = traktContent
        }
    }

    @Environment(\.appTheme) private var appTheme

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
                pageBody(for: state.currentPage)
                Spacer().frame(height: appTheme.spacing.xLarge)
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
                title: toolbarTitle(state.currentPage),
                opacity: 1.0,
                leadingIcon: {
                    GlassButton(icon: "chevron.left", action: onBack)
                }
            ),
            alignment: .top
        )
        .edgesIgnoringSafeArea(.top)
    }

    private func toolbarTitle(_ page: SettingsPageRoute) -> String {
        page == .root ? state.rootTitle : title(for: page)
    }

    // MARK: - Root

    private var rootContent: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.large) {
            ForEach(state.rootSections) { section in
                VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                    sectionLabel(section.label)
                    settingsCard {
                        ForEach(Array(section.items.enumerated()), id: \.element.id) { index, item in
                            navigationRow(item)
                            if index != section.items.count - 1 {
                                rowDivider
                            }
                        }
                    }
                }
            }

            Text(state.versionFooter)
                .textStyle(appTheme.typography.bodySmall)
                .foregroundColor(appTheme.colors.onSurfaceVariant)
                .frame(maxWidth: .infinity, alignment: .center)
                .padding(.vertical, appTheme.spacing.medium)
        }
    }

    // MARK: - Sub pages

    @ViewBuilder
    private func pageBody(for page: SettingsPageRoute) -> some View {
        switch page {
        case .root: rootContent
        case .appearance: appearancePage
        case .behavior: togglesPage(state.behaviorToggles)
        case .notifications: togglesPage(state.notificationToggles)
        case .privacy: privacyPage
        case .info: infoPage
        case .licenses: licensesPage
        case .trakt: traktPage
        }
    }

    private var appearancePage: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.large) {
            VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                sectionLabel(state.themeItem.title)
                settingsCard {
                    ThemeSelectorView(
                        themes: state.themeItem.themes,
                        selectedTheme: state.themeItem.selectedTheme,
                        onThemeSelected: state.themeItem.onThemeSelected
                    )
                    .padding(appTheme.spacing.medium)
                }
            }

            VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                sectionLabel(state.imageQualityItem.title)
                settingsCard {
                    VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                        HStack(spacing: appTheme.spacing.small) {
                            ForEach(state.imageQualityItem.options) { option in
                                SelectionChip(
                                    label: option.label,
                                    isSelected: option.id == state.imageQualityItem.selectedOptionId,
                                    action: option.onSelect
                                )
                            }
                        }
                        Text(state.imageQualityItem.subtitle)
                            .textStyle(appTheme.typography.bodySmall)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                    }
                    .padding(appTheme.spacing.medium)
                }
            }
        }
    }

    private func togglesPage(_ toggles: [SettingsToggleItem]) -> some View {
        settingsCard {
            ForEach(Array(toggles.enumerated()), id: \.element.id) { index, toggle in
                toggleRow(toggle)
                if index != toggles.count - 1 {
                    rowDivider
                }
            }
        }
    }

    private var traktPage: some View {
        let trakt = state.traktContent
        return VStack(alignment: .leading, spacing: appTheme.spacing.large) {
            settingsCard {
                HStack(spacing: appTheme.spacing.medium) {
                    Image("TraktLogo", bundle: .module)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 48, height: 48)
                    VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                        Text(trakt.title)
                            .textStyle(appTheme.typography.titleMedium)
                            .foregroundColor(appTheme.colors.onSurface)
                        Text(trakt.description)
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                    }
                    Spacer(minLength: 0)
                }
                .padding(appTheme.spacing.medium)
            }

            VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                sectionLabel(trakt.authenticationLabel)
                settingsCard {
                    VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                        Text(trakt.connectedTitle)
                            .textStyle(appTheme.typography.bodyLarge)
                            .foregroundColor(appTheme.colors.onSurface)
                        Text(trakt.connectedDescription)
                            .textStyle(appTheme.typography.bodyMedium)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                        Button(action: trakt.isAuthenticated ? trakt.onLogout : trakt.onLogin) {
                            Text(trakt.isAuthenticated ? trakt.logoutLabel : trakt.loginLabel)
                                .textStyle(appTheme.typography.labelLarge)
                                .foregroundColor(appTheme.colors.onSecondary)
                                .padding(.horizontal, appTheme.spacing.large)
                                .padding(.vertical, appTheme.spacing.small)
                                .background(appTheme.colors.secondary)
                                .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.medium))
                        }
                        .buttonStyle(.plain)
                    }
                    .padding(appTheme.spacing.medium)
                }
            }
        }
    }

    private var privacyPage: some View {
        settingsCard {
            ForEach(state.privacyToggles) { toggle in
                toggleRow(toggle)
            }
            ForEach(state.privacyLinks) { item in
                rowDivider
                navigationRow(item)
            }
        }
    }

    private var infoPage: some View {
        let info = state.infoContent
        return VStack(spacing: appTheme.spacing.large) {
            VStack(spacing: appTheme.spacing.medium) {
                info.icon
                    .resizable()
                    .scaledToFit()
                    .frame(width: 72, height: 72)
                    .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.large))

                Text(info.appName)
                    .textStyle(appTheme.typography.headlineSmall)
                    .foregroundColor(appTheme.colors.onSurface)

                Text(info.versionText)
                    .textStyle(appTheme.typography.bodyLarge)
                    .foregroundColor(appTheme.colors.secondary)
                    .onTapGesture(perform: info.onVersionTap)
            }
            .frame(maxWidth: .infinity)
            .padding(.top, appTheme.spacing.large)

            Text(info.description)
                .textStyle(appTheme.typography.bodyMedium)
                .foregroundColor(appTheme.colors.onSurfaceVariant)
                .multilineTextAlignment(.center)

            settingsCard {
                linkRow(
                    SettingsLinkItem(
                        id: "source",
                        title: info.sourceCodeLabel,
                        body: info.sourceCodeValue,
                        link: info.sourceCodeValue,
                        onOpen: info.onSourceCodeTap
                    )
                )
            }

            Text(info.apiDisclaimer)
                .textStyle(appTheme.typography.bodySmall)
                .foregroundColor(appTheme.colors.onSurfaceVariant)
                .multilineTextAlignment(.center)
        }
    }

    private var licensesPage: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.large) {
            ForEach(state.licenseSections) { section in
                VStack(alignment: .leading, spacing: appTheme.spacing.small) {
                    sectionLabel(section.label)
                    settingsCard {
                        ForEach(Array(section.items.enumerated()), id: \.element.id) { index, item in
                            linkRow(item)
                            if index != section.items.count - 1 {
                                rowDivider
                            }
                        }
                    }
                }
            }
        }
    }

    // MARK: - Rows

    private func navigationRow(_ item: SettingsNavigationItem) -> some View {
        Button(action: item.onTap) {
            HStack(spacing: appTheme.spacing.medium) {
                iconChip(item.icon)

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(item.title)
                        .textStyle(appTheme.typography.bodyLarge)
                        .foregroundColor(appTheme.colors.onSurface)
                    if let subtitle = item.subtitle {
                        Text(subtitle)
                            .textStyle(appTheme.typography.bodySmall)
                            .foregroundColor(appTheme.colors.onSurfaceVariant)
                    }
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .foregroundColor(appTheme.colors.onSurfaceVariant)
            }
            .padding(.horizontal, appTheme.spacing.medium)
            .padding(.vertical, appTheme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    private func toggleRow(_ item: SettingsToggleItem) -> some View {
        HStack(spacing: appTheme.spacing.medium) {
            iconChip(item.icon)

            VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                Text(item.title)
                    .textStyle(appTheme.typography.bodyLarge)
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
        .padding(.horizontal, appTheme.spacing.medium)
        .padding(.vertical, appTheme.spacing.small)
    }

    private func linkRow(_ item: SettingsLinkItem) -> some View {
        Button(action: item.onOpen) {
            HStack(alignment: .top, spacing: appTheme.spacing.medium) {
                if let asset = item.leadingAsset {
                    Image(asset, bundle: .module)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 36, height: 36)
                } else if let leading = item.leadingSystemImage {
                    Image(systemName: leading)
                        .foregroundColor(appTheme.colors.secondary)
                        .frame(width: appTheme.spacing.large, height: appTheme.spacing.large)
                }

                VStack(alignment: .leading, spacing: appTheme.spacing.xxSmall) {
                    Text(item.title)
                        .textStyle(appTheme.typography.titleMedium)
                        .foregroundColor(appTheme.colors.onSurface)
                    Text(item.body)
                        .textStyle(appTheme.typography.bodyMedium)
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                    Text(item.link)
                        .textStyle(appTheme.typography.bodySmall)
                        .foregroundColor(appTheme.colors.secondary)
                        .lineLimit(1)
                }

                Spacer()

                Image(systemName: "arrow.up.right.square")
                    .foregroundColor(appTheme.colors.onSurfaceVariant)
            }
            .padding(.horizontal, appTheme.spacing.medium)
            .padding(.vertical, appTheme.spacing.small)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    // MARK: - Building blocks

    private func sectionLabel(_ title: String) -> some View {
        Text(title)
            .textStyle(appTheme.typography.labelMedium)
            .foregroundColor(appTheme.colors.onSurfaceVariant)
            .padding(.leading, appTheme.spacing.small)
    }

    private func settingsCard(@ViewBuilder content: () -> some View) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(appTheme.colors.surface)
        .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.large))
        .overlay(
            RoundedRectangle(cornerRadius: appTheme.shapes.large)
                .stroke(appTheme.colors.outline.opacity(0.2), lineWidth: 0.5)
        )
    }

    private var rowDivider: some View {
        Rectangle()
            .fill(appTheme.colors.outline.opacity(0.2))
            .frame(height: 0.5)
            .padding(.leading, 64)
    }

    private func iconChip(_ systemName: String) -> some View {
        RoundedRectangle(cornerRadius: appTheme.shapes.medium)
            .fill(appTheme.colors.secondary.opacity(0.12))
            .frame(width: 36, height: 36)
            .overlay(
                Image(systemName: systemName)
                    .foregroundColor(appTheme.colors.secondary)
            )
    }

    private func title(for route: SettingsPageRoute) -> String {
        for section in state.rootSections {
            if let item = section.items.first(where: { $0.id == route.rawValue }) {
                return item.title
            }
        }
        return state.rootTitle
    }

    private var toolbarInset: CGFloat {
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        // GlassToolbar is `56 + safeAreaTop` tall; clear it with a small gap so the
        // first card is not tucked under the toolbar.
        return 56 + safeAreaTop + appTheme.spacing.small
    }
}
