import Components
import DesignSystem
import Models
import Settings
import SnapshotTestingLib
import SwiftUI
import XCTest

extension SettingsScreenTest {
    var sampleThemes: [ThemeItemModel] {
        [
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
    }

    var defaultThemeItem: SettingsThemeItem<ThemeItemModel> {
        SettingsThemeItem(
            icon: "paintpalette",
            title: "Theme",
            subtitle: "Choose your preferred theme",
            themes: sampleThemes,
            selectedTheme: sampleThemes[0],
            onThemeSelected: { _ in }
        )
    }

    var customThemeItem: SettingsThemeItem<ThemeItemModel> {
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

    var defaultImageQualityItem: SettingsImageQualityItem {
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

    var layoutToggles: [SettingsToggleItem] {
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

    func fontSizeItem(percent: Int = 100) -> SettingsFontSizeItem {
        SettingsFontSizeItem(
            title: "Font Size",
            description: "Adjust text size across the app",
            previewText: "The quick brown fox jumps over the lazy dog",
            resetLabel: "Reset",
            percent: percent,
            onPercentChange: { _ in }
        )
    }

    var discoverSectionsNavItem: SettingsNavigationItem {
        SettingsNavigationItem(
            id: SettingsPageRoute.discoverSections.rawValue,
            icon: SettingsPageRoute.discoverSections.iconName,
            title: "Discover Sections",
            subtitle: "Choose which sections appear on Discover",
            onTap: {}
        )
    }

    var discoverSectionToggles: [SettingsToggleItem] {
        [
            SettingsToggleItem(
                id: "START_WATCHING",
                icon: "play.circle",
                title: "Start Watching",
                subtitle: "",
                isOn: true,
                onToggle: { _ in }
            ),
            SettingsToggleItem(
                id: "TRENDING_TODAY",
                icon: "flame",
                title: "Trending Today",
                subtitle: "",
                isOn: true,
                onToggle: { _ in }
            ),
            SettingsToggleItem(
                id: "UPCOMING",
                icon: "calendar",
                title: "Upcoming",
                subtitle: "",
                isOn: false,
                onToggle: { _ in }
            ),
            SettingsToggleItem(
                id: "POPULAR",
                icon: "star",
                title: "Popular",
                subtitle: "",
                isOn: true,
                onToggle: { _ in }
            ),
            SettingsToggleItem(
                id: "TOP_RATED",
                icon: "trophy",
                title: "Top Rated",
                subtitle: "",
                isOn: true,
                onToggle: { _ in }
            ),
        ]
    }

    func posterStyleItem(locked: Bool = false) -> SettingsPosterStyleItem {
        let widthOptions: [SettingsPosterStyleOption] = [
            SettingsPosterStyleOption(id: "COMPACT", label: "Compact", onSelect: {}),
            SettingsPosterStyleOption(id: "STANDARD", label: "Standard", onSelect: {}),
            SettingsPosterStyleOption(id: "COMFORTABLE", label: "Comfortable", onSelect: {}),
            SettingsPosterStyleOption(id: "LARGE", label: "Large", onSelect: {}),
        ]
        let cornerOptions: [SettingsPosterStyleOption] = [
            SettingsPosterStyleOption(id: "SHARP", label: "Sharp", onSelect: {}),
            SettingsPosterStyleOption(id: "CLASSIC", label: "Classic", onSelect: {}),
            SettingsPosterStyleOption(id: "ROUNDED", label: "Rounded", onSelect: {}),
            SettingsPosterStyleOption(id: "PILL", label: "Pill", onSelect: {}),
        ]
        return SettingsPosterStyleItem(
            title: "Poster style",
            description: "Choose poster size and corner style",
            livePreviewLabel: "Live preview",
            resetLabel: "Reset",
            postersLabel: "Posters",
            landscapeLabel: "Landscape",
            cornerLabel: "Corner style",
            postersOptions: widthOptions,
            landscapeOptions: widthOptions,
            cornerOptions: cornerOptions,
            selectedPostersId: "STANDARD",
            selectedLandscapeId: "STANDARD",
            selectedCornerId: "SHARP",
            posterScale: 1,
            landscapeScale: 1,
            cornerRadius: 0,
            isLocked: locked,
            lockedBadgeText: locked ? "Premium" : "",
            lockedActionText: locked ? "Upgrade to Premium" : "",
            lockedAccessibilityLabel: locked ? "Locked" : ""
        )
    }

    var posterStyleNavItem: SettingsNavigationItem {
        SettingsNavigationItem(
            id: SettingsPageRoute.posterStyle.rawValue,
            icon: SettingsPageRoute.posterStyle.iconName,
            title: "Poster style",
            subtitle: "Choose poster size and corner style",
            onTap: {}
        )
    }
}
