import Components
import DesignSystem
import Models
import Settings
import SnapshotTestingLib
import SwiftUI
import XCTest

class SettingsScreenTest: SnapshotTestCase {
    private let sampleThemes: [ThemeItemModel] = [
        ThemeItemModel(
            id: "system",
            displayName: "System",
            backgroundColor: TvManiacColorScheme.light.background,
            accentColor: TvManiacColorScheme.light.secondary,
            onAccentColor: TvManiacColorScheme.light.onSecondary,
            isSystemTheme: true
        ),
        ThemeItemModel(
            id: "light",
            displayName: "Light",
            backgroundColor: TvManiacColorScheme.light.background,
            accentColor: TvManiacColorScheme.light.secondary,
            onAccentColor: TvManiacColorScheme.light.onSecondary
        ),
        ThemeItemModel(
            id: "dark",
            displayName: "Dark",
            backgroundColor: TvManiacColorScheme.dark.background,
            accentColor: TvManiacColorScheme.dark.secondary,
            onAccentColor: TvManiacColorScheme.dark.onSecondary
        ),
        ThemeItemModel(
            id: "autumn",
            displayName: "Autumn",
            backgroundColor: TvManiacColorScheme.autumn.background,
            accentColor: TvManiacColorScheme.autumn.secondary,
            onAccentColor: TvManiacColorScheme.autumn.onSecondary,
            isPremium: true
        ),
        ThemeItemModel(
            id: "aqua",
            displayName: "Aqua",
            backgroundColor: TvManiacColorScheme.aqua.background,
            accentColor: TvManiacColorScheme.aqua.secondary,
            onAccentColor: TvManiacColorScheme.aqua.onSecondary,
            isPremium: true
        ),
        ThemeItemModel(
            id: "amber",
            displayName: "Amber",
            backgroundColor: TvManiacColorScheme.amber.background,
            accentColor: TvManiacColorScheme.amber.secondary,
            onAccentColor: TvManiacColorScheme.amber.onSecondary,
            isPremium: true
        ),
        ThemeItemModel(
            id: "snow",
            displayName: "Snow",
            backgroundColor: TvManiacColorScheme.snow.background,
            accentColor: TvManiacColorScheme.snow.secondary,
            onAccentColor: TvManiacColorScheme.snow.onSecondary,
            isPremium: true
        ),
        ThemeItemModel(
            id: "terminal",
            displayName: "Terminal",
            backgroundColor: TvManiacColorScheme.terminal.background,
            accentColor: TvManiacColorScheme.terminal.secondary,
            onAccentColor: TvManiacColorScheme.terminal.onSecondary,
            isPremium: true
        ),
        ThemeItemModel(
            id: "crimson",
            displayName: "Crimson",
            backgroundColor: TvManiacColorScheme.crimson.background,
            accentColor: TvManiacColorScheme.crimson.secondary,
            onAccentColor: TvManiacColorScheme.crimson.onSecondary,
            isPremium: true
        ),
    ]

    private var defaultThemeItem: SettingsThemeItem<ThemeItemModel> {
        SettingsThemeItem(
            icon: "paintpalette",
            title: "Theme",
            subtitle: "Choose your preferred theme",
            themes: sampleThemes,
            selectedTheme: sampleThemes[0],
            onThemeSelected: { _ in }
        )
    }

    private var customThemeItem: SettingsThemeItem<ThemeItemModel> {
        SettingsThemeItem(
            icon: "paintpalette",
            title: "Theme",
            subtitle: "Choose your preferred theme",
            themes: sampleThemes,
            selectedTheme: sampleThemes[0],
            isCustomThemesLocked: true,
            lockedBadgeText: "Premium",
            lockedTitle: "Custom themes are a Premium feature",
            lockedMessage: "Upgrade to Premium to use custom themes.",
            lockedActionText: "Upgrade to Premium",
            lockedAccessibilityLabel: "Locked",
            onThemeSelected: { _ in }
        )
    }

    private var defaultImageQualityItem: SettingsImageQualityItem {
        SettingsImageQualityItem(
            icon: "photo",
            title: "Image Quality",
            subtitle: "Automatically adjusts based on network",
            options: [
                SettingsImageQualityOption(id: "AUTO", label: "Auto", onSelect: {}),
                SettingsImageQualityOption(id: "HIGH", label: "High", onSelect: {}),
                SettingsImageQualityOption(id: "MEDIUM", label: "Medium", onSelect: {}),
                SettingsImageQualityOption(id: "LOW", label: "Low", onSelect: {}),
            ],
            selectedOptionId: "AUTO"
        )
    }

    private var layoutToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "haptic",
                icon: "iphone.radiowaves.left.and.right",
                title: "Haptic feedback",
                subtitle: "Feel subtle vibrations during interactions",
                isOn: true,
                onToggle: { _ in }
            ),
            SettingsToggleItem(
                id: "season-order",
                icon: "arrow.up.arrow.down",
                title: "Season Order",
                subtitle: "Order the latest season first",
                isOn: false,
                onToggle: { _ in }
            ),
            SettingsToggleItem(
                id: "blur-unwatched",
                icon: "eye.slash",
                title: "Hide Spoilers",
                subtitle: "Hide spoilers for unwatched episodes",
                isOn: false,
                onToggle: { _ in }
            ),
        ]
    }

    private var behaviorToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "sync",
                icon: "arrow.triangle.2.circlepath",
                title: "Background Sync",
                subtitle: "Sync your library in the background",
                isOn: false,
                onToggle: { _ in }
            ),
            SettingsToggleItem(
                id: "specials",
                icon: "film.stack",
                title: "Include Specials",
                subtitle: "Show special episodes in season lists",
                isOn: true,
                onToggle: { _ in }
            ),
            SettingsToggleItem(
                id: "youtube",
                icon: "tv",
                title: "Open in YouTube",
                subtitle: "Open trailers in the YouTube app",
                isOn: false,
                onToggle: { _ in }
            ),
        ]
    }

    private var notificationToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "notifications",
                icon: "bell.fill",
                title: "Episode Notifications",
                subtitle: "Get notified when new episodes air",
                isOn: true,
                onToggle: { _ in }
            ),
        ]
    }

    private var lockedNotificationToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "notifications",
                icon: "bell.fill",
                title: "Episode Notifications",
                subtitle: "Get notified when new episodes air",
                isOn: false,
                isLocked: true,
                lockedBadgeText: "Premium",
                lockedAccessibilityLabel: "Locked",
                onToggle: { _ in }
            ),
        ]
    }

    private var privacyToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "crash-reporting",
                icon: "ladybug",
                title: "Crash Reporting",
                subtitle: "Help improve the app by sending crash reports",
                isOn: true,
                onToggle: { _ in }
            ),
        ]
    }

    private var privacyLinks: [SettingsNavigationItem] {
        [
            SettingsNavigationItem(
                id: "privacy-policy",
                icon: "hand.raised",
                title: "Privacy Policy",
                onTap: {}
            ),
        ]
    }

    private var infoContent: SettingsInfoContent {
        SettingsInfoContent(
            icon: Image(systemName: "app.fill"),
            appName: "TvManiac",
            versionText: "Version 1.0.0",
            description: "A Kotlin Multiplatform app for discovering and tracking your favorite TV shows.",
            sourceCodeLabel: "Source Code",
            sourceCodeValue: "GitHub",
            apiDisclaimer: "This product uses the TMDB and Trakt API but is not endorsed or certified by either.",
            onVersionTap: {},
            onSourceCodeTap: {}
        )
    }

    private var licenseSections: [SettingsLicenseSection] {
        [
            SettingsLicenseSection(id: "app", label: "App", items: [
                SettingsLinkItem(
                    id: "tvmaniac",
                    title: "TvManiac",
                    body: "Open-source on GitHub.",
                    link: "https://github.com/c0de-wizard/tv-maniac",
                    onOpen: {}
                ),
            ]),
            SettingsLicenseSection(id: "data", label: "Data & Services", items: [
                SettingsLinkItem(
                    id: "tmdb",
                    leadingAsset: "TmdbLogo",
                    title: "The Movie Database (TMDB)",
                    body: "This product uses the TMDB API but is not endorsed or certified by TMDB.",
                    link: "https://www.themoviedb.org",
                    onOpen: {}
                ),
                SettingsLinkItem(
                    id: "trakt",
                    leadingAsset: "TraktLogo",
                    title: "Trakt",
                    body: "Syncs your watch history, watchlist, and episode progress.",
                    link: "https://trakt.tv",
                    onOpen: {}
                ),
            ]),
        ]
    }

    private func accountContent(
        authenticated: Bool,
        withSwitchAffordance: Bool = false,
        isSwitching: Bool = false,
        showSwitchConfirmation: Bool = false,
        isProcessingAuth: Bool = false
    ) -> SettingsAccountContent {
        let switchLabel: String? = withSwitchAffordance || isSwitching || showSwitchConfirmation
            ? "Switch to Simkl"
            : nil
        return SettingsAccountContent(
            title: "Trakt",
            description: "Sync your watchlist, watch progress, continue watching, and personal lists with Trakt.",
            authenticationLabel: "Connect & Sync Your Content",
            connectTitle: "Connect",
            syncDescription: "Save your progress, discover new titles, and sync your content across all devices.",
            connectedTitle: authenticated ? "Connected" : "Connect to Trakt",
            connectedDescription: authenticated
                ? "Your watch history, watchlist, and episode progress sync with Trakt."
                : "Sign in with Trakt to sync your watch history, watchlist, and episode progress across your devices.",
            isAuthenticated: authenticated,
            isProcessingAuth: isProcessingAuth,
            logoutLabel: "Logout",
            loginLabel: "Login",
            providerName: "Trakt",
            authProviders: [
                SwiftAuthProvider(id: "TRAKT", label: "Continue with Trakt", logoName: "TraktMono"),
                SwiftAuthProvider(id: "SIMKL", label: "Continue with Simkl", logoName: "SimklMono"),
            ],
            switchTargetLogoName: switchLabel != nil ? "SimklMono" : nil,
            switchActionLabel: switchLabel,
            isSwitching: isSwitching,
            showSwitchConfirmation: showSwitchConfirmation,
            switchDialogTitle: showSwitchConfirmation ? "Switch to Simkl?" : nil,
            switchDialogMessage: showSwitchConfirmation
                ? "You have 3 unsaved changes. Switching providers will discard them."
                : nil,
            switchConfirmLabel: "Switch",
            switchCancelLabel: "Cancel",
            switchingLabel: "Switching...",
            onLogout: {},
            onProviderSelected: { _ in },
            onSwitchProvider: {},
            onConfirmSwitch: {},
            onDismissSwitchDialog: {}
        )
    }

    private func rootSections(authenticated: Bool) -> [SettingsRootSection] {
        var sections: [SettingsRootSection] = []
        if authenticated {
            sections.append(SettingsRootSection(id: "account", label: "Account", items: [
                navItem(.account, "Trakt Account", "Manage your Trakt connection"),
            ]))
        }
        sections.append(SettingsRootSection(id: "general", label: "General", items: [
            navItem(.appearance, "Appearance", "Theme and image quality"),
            navItem(.layout, "Layout", "Personalize how shows and episodes look"),
            navItem(.behavior, "Behavior", "Sync, specials, and trailers"),
            navItem(.notifications, "Notifications", "Episode release alerts"),
            navItem(.privacy, "Privacy", "Crash reporting and privacy policy"),
        ]))
        sections.append(SettingsRootSection(id: "about", label: "About", items: [
            navItem(.info, "Info", "App version and source code"),
            navItem(.licenses, "Licenses & Attribution", "Data sources and acknowledgements"),
        ]))
        return sections
    }

    private func navItem(_ route: SettingsPageRoute, _ title: String, _ subtitle: String) -> SettingsNavigationItem {
        SettingsNavigationItem(id: route.rawValue, icon: route.iconName, title: title, subtitle: subtitle, onTap: {})
    }

    private func makeState(
        page: SettingsPageRoute,
        authenticated: Bool,
        isLoading: Bool = false,
        customAccountContent: SettingsAccountContent? = nil,
        customThemeItem: SettingsThemeItem<ThemeItemModel>? = nil,
        customNotificationToggles: [SettingsToggleItem]? = nil
    ) -> SettingsScreen<ThemeItemModel>.State {
        SettingsScreen<ThemeItemModel>.State(
            isLoading: isLoading,
            rootTitle: "Settings",
            currentPage: page,
            rootSections: rootSections(authenticated: authenticated),
            themeItem: customThemeItem ?? defaultThemeItem,
            imageQualityItem: defaultImageQualityItem,
            layoutToggles: layoutToggles,
            behaviorToggles: behaviorToggles,
            notificationToggles: customNotificationToggles ?? notificationToggles,
            privacyToggles: privacyToggles,
            privacyLinks: privacyLinks,
            infoContent: infoContent,
            licenseSections: licenseSections,
            accountContent: customAccountContent ?? accountContent(authenticated: authenticated)
        )
    }

    func test_SettingsScreen_Loading() {
        SettingsScreen(state: makeState(page: .root, authenticated: false, isLoading: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Loading")
    }

    func test_SettingsScreen_Root() {
        SettingsScreen(state: makeState(page: .root, authenticated: false), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Root")
    }

    func test_SettingsScreen_RootAuthenticated() {
        SettingsScreen(state: makeState(page: .root, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_RootAuthenticated")
    }

    func test_SettingsScreen_Layout() {
        SettingsScreen(state: makeState(page: .layout, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Layout")
    }

    func test_SettingsScreen_Appearance() {
        SettingsScreen(state: makeState(page: .appearance, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Appearance")
    }

    func test_SettingsScreen_Appearance_Locked() {
        SettingsScreen(
            state: makeState(
                page: .appearance,
                authenticated: true,
                customThemeItem: customThemeItem
            ),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Appearance_Locked")
    }

    func test_SettingsScreen_Behavior() {
        SettingsScreen(state: makeState(page: .behavior, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Behavior")
    }

    func test_SettingsScreen_Notifications() {
        SettingsScreen(state: makeState(page: .notifications, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Notifications")
    }

    func test_SettingsScreen_Notifications_Locked() {
        SettingsScreen(
            state: makeState(
                page: .notifications,
                authenticated: true,
                customNotificationToggles: lockedNotificationToggles
            ),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Notifications_Locked")
    }

    func test_SettingsScreen_Privacy() {
        SettingsScreen(state: makeState(page: .privacy, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Privacy")
    }

    func test_SettingsScreen_Info() {
        SettingsScreen(state: makeState(page: .info, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Info")
    }

    func test_SettingsScreen_Licenses() {
        SettingsScreen(state: makeState(page: .licenses, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Licenses")
    }

    func test_SettingsScreen_Trakt() {
        SettingsScreen(state: makeState(page: .account, authenticated: true), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Trakt")
    }

    func test_SettingsScreen_TraktLoggedOut() {
        SettingsScreen(state: makeState(page: .account, authenticated: false), onBack: {})
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_TraktLoggedOut")
    }

    func test_SettingsScreen_Account_SwitchAffordance() {
        let content = accountContent(authenticated: true, withSwitchAffordance: true)
        SettingsScreen(
            state: makeState(page: .account, authenticated: true, customAccountContent: content),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Account_SwitchAffordance")
    }

    func test_SettingsScreen_Account_Switching() {
        let content = accountContent(authenticated: true, isSwitching: true)
        SettingsScreen(
            state: makeState(page: .account, authenticated: true, customAccountContent: content),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Account_Switching")
    }

    func test_SettingsScreen_Account_LoggingOut() {
        let content = accountContent(authenticated: true, isProcessingAuth: true)
        SettingsScreen(
            state: makeState(page: .account, authenticated: true, customAccountContent: content),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Account_LoggingOut")
    }

    func test_SettingsScreen_Account_SwitchConfirmDialog() {
        let content = accountContent(authenticated: true, showSwitchConfirmation: true)
        SettingsScreen(
            state: makeState(page: .account, authenticated: true, customAccountContent: content),
            onBack: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Account_SwitchConfirmDialog")
    }
}
