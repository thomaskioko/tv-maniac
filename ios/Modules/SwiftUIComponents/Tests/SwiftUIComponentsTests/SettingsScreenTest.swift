import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
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

    private var defaultBehaviorToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "notifications",
                icon: "bell.fill",
                title: "Episode Notifications",
                subtitle: "Get notified when new episodes air",
                isOn: true,
                onToggle: { _ in }
            ),
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

    private var defaultPrivacyToggles: [SettingsToggleItem] {
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

    private var defaultInfoItems: [SettingsNavigationItem] {
        [
            SettingsNavigationItem(
                id: "about",
                icon: "info.circle",
                title: "About",
                subtitle: "Learn more about TvManiac",
                onTap: {}
            ),
            SettingsNavigationItem(
                id: "privacy",
                icon: "hand.raised",
                title: "Privacy Policy",
                onTap: {}
            ),
        ]
    }

    func test_SettingsScreen_Default() {
        SettingsScreen(
            title: "Settings",
            themeItem: defaultThemeItem,
            imageQualityItem: defaultImageQualityItem,
            behaviorToggles: defaultBehaviorToggles,
            privacyToggles: defaultPrivacyToggles,
            infoItems: defaultInfoItems,
            onBack: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Default")
    }

    func test_SettingsScreen_Authenticated() {
        let traktItems = [
            SettingsNavigationItem(
                id: "logout",
                icon: "person.fill",
                title: "Logout",
                subtitle: "Sign out of Trakt",
                onTap: {}
            ),
        ]

        SettingsScreen(
            title: "Settings",
            themeItem: defaultThemeItem,
            imageQualityItem: defaultImageQualityItem,
            behaviorToggles: defaultBehaviorToggles,
            privacyToggles: defaultPrivacyToggles,
            infoItems: defaultInfoItems,
            traktItems: traktItems,
            onBack: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_Authenticated")
    }

    func test_SettingsScreen_WithSyncDate() {
        let togglesWithSync = [
            SettingsToggleItem(
                id: "notifications",
                icon: "bell.fill",
                title: "Episode Notifications",
                subtitle: "Get notified when new episodes air",
                isOn: true,
                onToggle: { _ in }
            ),
            SettingsToggleItem(
                id: "sync",
                icon: "arrow.triangle.2.circlepath",
                title: "Background Sync",
                subtitle: "Sync your library in the background",
                secondarySubtitle: "Last synced: 2 hours ago",
                isOn: true,
                onToggle: { _ in }
            ),
        ]

        SettingsScreen(
            title: "Settings",
            themeItem: defaultThemeItem,
            imageQualityItem: defaultImageQualityItem,
            behaviorToggles: togglesWithSync,
            privacyToggles: defaultPrivacyToggles,
            infoItems: defaultInfoItems,
            onBack: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_WithSyncDate")
    }

    func test_SettingsScreen_WithDebugMenu() {
        let debugItems = [
            SettingsNavigationItem(
                id: "debug",
                icon: "ellipsis.curlybraces",
                title: "Debug Menu",
                subtitle: "Developer tools and diagnostics",
                onTap: {}
            ),
        ]

        SettingsScreen(
            title: "Settings",
            themeItem: defaultThemeItem,
            imageQualityItem: defaultImageQualityItem,
            behaviorToggles: defaultBehaviorToggles,
            privacyToggles: defaultPrivacyToggles,
            infoItems: defaultInfoItems,
            debugItems: debugItems,
            onBack: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "SettingsScreen_WithDebugMenu")
    }
}
