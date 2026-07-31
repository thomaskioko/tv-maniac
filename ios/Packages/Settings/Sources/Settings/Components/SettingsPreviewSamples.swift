#if DEBUG
    import Components
    import DesignSystem
    import Models
    import SwiftUI

    enum SettingsPreviewSamples {
        static let themes: [ThemeItemModel] = [
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
                id: "aqua",
                displayName: "Aqua",
                backgroundColor: TvManiacColorScheme.aqua.background,
                accentColor: TvManiacColorScheme.aqua.secondary,
                onAccentColor: TvManiacColorScheme.aqua.onSecondary,
                isPremium: true
            ),
        ]

        static var themeItem: SettingsThemeItem<ThemeItemModel> {
            SettingsThemeItem(
                icon: "paintpalette",
                title: "Theme",
                subtitle: "Choose your preferred theme",
                themes: themes,
                selectedTheme: themes[0],
                onThemeSelected: { _ in }
            )
        }

        static var customThemeItem: SettingsThemeItem<ThemeItemModel> {
            SettingsThemeItem(
                icon: "paintpalette",
                title: "Theme",
                subtitle: "Choose your preferred theme",
                themes: themes,
                selectedTheme: themes[0],
                isCustomThemesLocked: true,
                lockedBadgeText: "Premium",
                lockedTitle: "Custom themes are a Premium feature",
                lockedMessage: "Upgrade to Premium to use custom themes.",
                lockedActionText: "Upgrade to Premium",
                lockedAccessibilityLabel: "Locked",
                onThemeSelected: { _ in }
            )
        }

        static var imageQualityItem: SettingsImageQualityItem {
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

        static var layoutToggles: [SettingsToggleItem] {
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

        static var fontSizeItem: SettingsFontSizeItem {
            SettingsFontSizeItem(
                title: "Font Size",
                description: "Adjust text size across the app",
                previewText: "The quick brown fox jumps over the lazy dog",
                resetLabel: "Reset",
                percent: 100,
                onPercentChange: { _ in }
            )
        }

        static var posterStyleNavItem: SettingsNavigationItem {
            SettingsNavigationItem(
                id: SettingsPageRoute.posterStyle.rawValue,
                icon: SettingsPageRoute.posterStyle.iconName,
                title: "Poster style",
                subtitle: "Choose poster size and corner style",
                onTap: {}
            )
        }

        static var posterStyleItem: SettingsPosterStyleItem {
            SettingsPosterStyleItem(
                title: "Poster style",
                description: "Choose poster size and corner style",
                livePreviewLabel: "Live preview",
                resetLabel: "Reset",
                postersLabel: "Posters",
                landscapeLabel: "Landscape",
                cornerLabel: "Corner style",
                postersOptions: posterWidthOptions,
                landscapeOptions: posterWidthOptions,
                cornerOptions: posterCornerOptions,
                selectedPostersId: "STANDARD",
                selectedLandscapeId: "STANDARD",
                selectedCornerId: "SHARP",
                posterScale: 1,
                landscapeScale: 1,
                cornerRadius: 0
            )
        }

        static var lockedPosterStyleItem: SettingsPosterStyleItem {
            SettingsPosterStyleItem(
                title: "Poster style",
                description: "Choose poster size and corner style",
                livePreviewLabel: "Live preview",
                resetLabel: "Reset",
                postersLabel: "Posters",
                landscapeLabel: "Landscape",
                cornerLabel: "Corner style",
                postersOptions: posterWidthOptions,
                landscapeOptions: posterWidthOptions,
                cornerOptions: posterCornerOptions,
                selectedPostersId: "STANDARD",
                selectedLandscapeId: "STANDARD",
                selectedCornerId: "SHARP",
                posterScale: 1,
                landscapeScale: 1,
                cornerRadius: 0,
                isLocked: true,
                lockedBadgeText: "Premium",
                lockedActionText: "Upgrade to Premium",
                lockedAccessibilityLabel: "Locked"
            )
        }

        private static var posterWidthOptions: [SettingsPosterStyleOption] {
            [
                SettingsPosterStyleOption(id: "COMPACT", label: "Compact", onSelect: {}),
                SettingsPosterStyleOption(id: "STANDARD", label: "Standard", onSelect: {}),
                SettingsPosterStyleOption(id: "COMFORTABLE", label: "Comfortable", onSelect: {}),
                SettingsPosterStyleOption(id: "LARGE", label: "Large", onSelect: {}),
            ]
        }

        private static var posterCornerOptions: [SettingsPosterStyleOption] {
            [
                SettingsPosterStyleOption(id: "SHARP", label: "Sharp", onSelect: {}),
                SettingsPosterStyleOption(id: "CLASSIC", label: "Classic", onSelect: {}),
                SettingsPosterStyleOption(id: "ROUNDED", label: "Rounded", onSelect: {}),
                SettingsPosterStyleOption(id: "PILL", label: "Pill", onSelect: {}),
            ]
        }
    }
#endif
