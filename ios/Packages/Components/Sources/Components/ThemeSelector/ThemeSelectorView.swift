import DesignSystem
import SwiftUI

public struct ThemeSelectorView<Theme: ThemeItem>: View {
    @Environment(\.appTheme) private var appTheme

    let themes: [Theme]
    let selectedTheme: Theme
    let isCustomThemesLocked: Bool
    let lockedBadgeText: String
    let lockedTitle: String
    let lockedMessage: String
    let lockedActionText: String
    let lockedAccessibilityLabel: String
    let onUpgradeClick: () -> Void
    let onThemeSelected: (Theme) -> Void

    public init(
        themes: [Theme],
        selectedTheme: Theme,
        isCustomThemesLocked: Bool = false,
        lockedBadgeText: String = "",
        lockedTitle: String = "",
        lockedMessage: String = "",
        lockedActionText: String = "",
        lockedAccessibilityLabel: String = "",
        onUpgradeClick: @escaping () -> Void = {},
        onThemeSelected: @escaping (Theme) -> Void
    ) {
        self.themes = themes
        self.selectedTheme = selectedTheme
        self.isCustomThemesLocked = isCustomThemesLocked
        self.lockedBadgeText = lockedBadgeText
        self.lockedTitle = lockedTitle
        self.lockedMessage = lockedMessage
        self.lockedActionText = lockedActionText
        self.lockedAccessibilityLabel = lockedAccessibilityLabel
        self.onUpgradeClick = onUpgradeClick
        self.onThemeSelected = onThemeSelected
    }

    private let columnsPerRow = 3

    private var freeThemes: [Theme] {
        themes.filter { !$0.isPremium }
    }

    private var premiumThemes: [Theme] {
        themes.filter(\.isPremium)
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: appTheme.spacing.small) {
            themeGrid(freeThemes)

            themeGrid(premiumThemes)
                .premiumOverlay(
                    isLocked: isCustomThemesLocked,
                    badgeText: lockedBadgeText,
                    title: lockedTitle.isEmpty ? nil : lockedTitle,
                    message: lockedMessage.isEmpty ? nil : lockedMessage,
                    actionText: lockedActionText,
                    onActionClick: onUpgradeClick,
                    accessibilityLabel: lockedAccessibilityLabel.isEmpty ? nil : lockedAccessibilityLabel
                )
        }
    }

    private func themeGrid(_ groupThemes: [Theme]) -> some View {
        VStack(spacing: appTheme.spacing.small) {
            ForEach(Array(rows(of: groupThemes).enumerated()), id: \.offset) { _, row in
                HStack(spacing: appTheme.spacing.small) {
                    ForEach(row, id: \.id) { theme in
                        ThemePreviewSwatch(
                            backgroundColor: theme.backgroundColor,
                            accentColor: theme.accentColor,
                            onAccentColor: theme.onAccentColor,
                            displayName: theme.displayName,
                            isSelected: theme.id == selectedTheme.id,
                            isSystemTheme: theme.isSystemTheme,
                            onSelect: { onThemeSelected(theme) }
                        )
                        .frame(maxWidth: .infinity)
                    }
                    if row.count < columnsPerRow {
                        ForEach(0 ..< (columnsPerRow - row.count), id: \.self) { _ in
                            Color.clear.frame(maxWidth: .infinity)
                        }
                    }
                }
            }
        }
    }

    private func rows(of groupThemes: [Theme]) -> [[Theme]] {
        stride(from: 0, to: groupThemes.count, by: columnsPerRow).map { start in
            Array(groupThemes[start ..< min(start + columnsPerRow, groupThemes.count)])
        }
    }
}

public protocol ThemeItem {
    var id: String { get }
    var displayName: String { get }
    var backgroundColor: Color { get }
    var accentColor: Color { get }
    var onAccentColor: Color { get }
    var isSystemTheme: Bool { get }
    var isPremium: Bool { get }
}

public extension ThemeItem {
    var isPremium: Bool {
        false
    }
}

public struct ThemeItemModel: ThemeItem, Identifiable {
    public let id: String
    public let displayName: String
    public let backgroundColor: Color
    public let accentColor: Color
    public let onAccentColor: Color
    public let isSystemTheme: Bool
    public let isPremium: Bool

    public init(
        id: String,
        displayName: String,
        backgroundColor: Color,
        accentColor: Color,
        onAccentColor: Color,
        isSystemTheme: Bool = false,
        isPremium: Bool = false
    ) {
        self.id = id
        self.displayName = displayName
        self.backgroundColor = backgroundColor
        self.accentColor = accentColor
        self.onAccentColor = onAccentColor
        self.isSystemTheme = isSystemTheme
        self.isPremium = isPremium
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
            onAccentColor: TvManiacColorScheme.terminal.onSecondary,
            isPremium: true
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
            id: "crimson",
            displayName: "Crimson",
            backgroundColor: TvManiacColorScheme.crimson.background,
            accentColor: TvManiacColorScheme.crimson.secondary,
            onAccentColor: TvManiacColorScheme.crimson.onSecondary,
            isPremium: true
        ),
    ]

    return ThemeSelectorView(
        themes: themes,
        selectedTheme: themes[0],
        onThemeSelected: { _ in }
    )
    .padding()
    .background(.appBackground)
}

#Preview("Locked") {
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
            id: "terminal",
            displayName: "Terminal",
            backgroundColor: TvManiacColorScheme.terminal.background,
            accentColor: TvManiacColorScheme.terminal.secondary,
            onAccentColor: TvManiacColorScheme.terminal.onSecondary,
            isPremium: true
        ),
        ThemeItemModel(
            id: "autumn",
            displayName: "Autumn",
            backgroundColor: TvManiacColorScheme.autumn.background,
            accentColor: TvManiacColorScheme.autumn.secondary,
            onAccentColor: TvManiacColorScheme.autumn.onSecondary,
            isPremium: true
        ),
    ]

    return ThemeSelectorView(
        themes: themes,
        selectedTheme: themes[0],
        isCustomThemesLocked: true,
        lockedBadgeText: "Premium",
        lockedAccessibilityLabel: "Locked",
        onThemeSelected: { _ in }
    )
    .padding()
    .background(.appBackground)
}
