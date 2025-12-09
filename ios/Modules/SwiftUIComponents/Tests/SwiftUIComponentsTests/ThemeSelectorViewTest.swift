import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ThemeSelectorViewTest: SnapshotTestCase {
    func test_ThemeSelectorView() {
        let themes = [
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
                id: "terminal",
                displayName: "Terminal",
                backgroundColor: TvManiacColorScheme.terminal.background,
                accentColor: TvManiacColorScheme.terminal.secondary,
                onAccentColor: TvManiacColorScheme.terminal.onSecondary
            ),
            ThemeItemModel(
                id: "autumn",
                displayName: "Autumn",
                backgroundColor: TvManiacColorScheme.autumn.background,
                accentColor: TvManiacColorScheme.autumn.secondary,
                onAccentColor: TvManiacColorScheme.autumn.onSecondary
            ),
            ThemeItemModel(
                id: "aqua",
                displayName: "Aqua",
                backgroundColor: TvManiacColorScheme.aqua.background,
                accentColor: TvManiacColorScheme.aqua.secondary,
                onAccentColor: TvManiacColorScheme.aqua.onSecondary
            ),
            ThemeItemModel(
                id: "amber",
                displayName: "Amber",
                backgroundColor: TvManiacColorScheme.amber.background,
                accentColor: TvManiacColorScheme.amber.secondary,
                onAccentColor: TvManiacColorScheme.amber.onSecondary
            ),
            ThemeItemModel(
                id: "snow",
                displayName: "Snow",
                backgroundColor: TvManiacColorScheme.snow.background,
                accentColor: TvManiacColorScheme.snow.secondary,
                onAccentColor: TvManiacColorScheme.snow.onSecondary
            ),
            ThemeItemModel(
                id: "crimson",
                displayName: "Crimson",
                backgroundColor: TvManiacColorScheme.crimson.background,
                accentColor: TvManiacColorScheme.crimson.secondary,
                onAccentColor: TvManiacColorScheme.crimson.onSecondary
            ),
        ]

        ThemeSelectorView(
            themes: themes,
            selectedTheme: themes[2],
            onThemeSelected: { _ in }
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "ThemeSelectorView")
    }
}
