import SwiftUI

public struct ThemeSelectorView<Theme: ThemeItem>: View {
    let themes: [Theme]
    let selectedTheme: Theme
    let onThemeSelected: (Theme) -> Void

    public init(
        themes: [Theme],
        selectedTheme: Theme,
        onThemeSelected: @escaping (Theme) -> Void
    ) {
        self.themes = themes
        self.selectedTheme = selectedTheme
        self.onThemeSelected = onThemeSelected
    }

    private let columns = [
        GridItem(.flexible(), spacing: 8),
        GridItem(.flexible(), spacing: 8),
        GridItem(.flexible(), spacing: 8),
    ]

    public var body: some View {
        LazyVGrid(columns: columns, spacing: 12) {
            ForEach(themes, id: \.id) { theme in
                ThemePreviewSwatch(
                    backgroundColor: theme.backgroundColor,
                    accentColor: theme.accentColor,
                    onAccentColor: theme.onAccentColor,
                    displayName: theme.displayName,
                    isSelected: theme.id == selectedTheme.id,
                    isSystemTheme: theme.isSystemTheme,
                    onSelect: { onThemeSelected(theme) }
                )
            }
        }
        .padding(.horizontal, 8)
    }
}

public protocol ThemeItem {
    var id: String { get }
    var displayName: String { get }
    var backgroundColor: Color { get }
    var accentColor: Color { get }
    var onAccentColor: Color { get }
    var isSystemTheme: Bool { get }
}

public struct ThemeItemModel: ThemeItem, Identifiable {
    public let id: String
    public let displayName: String
    public let backgroundColor: Color
    public let accentColor: Color
    public let onAccentColor: Color
    public let isSystemTheme: Bool

    public init(
        id: String,
        displayName: String,
        backgroundColor: Color,
        accentColor: Color,
        onAccentColor: Color,
        isSystemTheme: Bool = false
    ) {
        self.id = id
        self.displayName = displayName
        self.backgroundColor = backgroundColor
        self.accentColor = accentColor
        self.onAccentColor = onAccentColor
        self.isSystemTheme = isSystemTheme
    }
}

#Preview {
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

    return ThemeSelectorView(
        themes: themes,
        selectedTheme: themes[0],
        onThemeSelected: { _ in }
    )
    .padding()
    .background(Color(.systemBackground))
}
