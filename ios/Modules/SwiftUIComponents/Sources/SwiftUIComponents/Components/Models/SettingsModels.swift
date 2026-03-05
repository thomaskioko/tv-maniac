import Foundation

public struct SettingsToggleItem: Identifiable {
    public let id: String
    public let icon: String
    public let title: String
    public let subtitle: String
    public let secondarySubtitle: String?
    public let isOn: Bool
    public let onToggle: (Bool) -> Void

    public init(
        id: String,
        icon: String,
        title: String,
        subtitle: String,
        secondarySubtitle: String? = nil,
        isOn: Bool,
        onToggle: @escaping (Bool) -> Void
    ) {
        self.id = id
        self.icon = icon
        self.title = title
        self.subtitle = subtitle
        self.secondarySubtitle = secondarySubtitle
        self.isOn = isOn
        self.onToggle = onToggle
    }
}

extension SettingsToggleItem: Equatable {
    public static func == (lhs: SettingsToggleItem, rhs: SettingsToggleItem) -> Bool {
        lhs.id == rhs.id
            && lhs.icon == rhs.icon
            && lhs.title == rhs.title
            && lhs.subtitle == rhs.subtitle
            && lhs.secondarySubtitle == rhs.secondarySubtitle
            && lhs.isOn == rhs.isOn
    }
}

public struct SettingsNavigationItem: Identifiable {
    public let id: String
    public let icon: String
    public let title: String
    public let subtitle: String?
    public let onTap: () -> Void

    public init(
        id: String,
        icon: String,
        title: String,
        subtitle: String? = nil,
        onTap: @escaping () -> Void
    ) {
        self.id = id
        self.icon = icon
        self.title = title
        self.subtitle = subtitle
        self.onTap = onTap
    }
}

extension SettingsNavigationItem: Equatable {
    public static func == (lhs: SettingsNavigationItem, rhs: SettingsNavigationItem) -> Bool {
        lhs.id == rhs.id
            && lhs.icon == rhs.icon
            && lhs.title == rhs.title
            && lhs.subtitle == rhs.subtitle
    }
}

public struct SettingsImageQualityItem: Equatable {
    public let icon: String
    public let title: String
    public let subtitle: String
    public let options: [SettingsImageQualityOption]
    public let selectedOptionId: String

    public init(
        icon: String,
        title: String,
        subtitle: String,
        options: [SettingsImageQualityOption],
        selectedOptionId: String
    ) {
        self.icon = icon
        self.title = title
        self.subtitle = subtitle
        self.options = options
        self.selectedOptionId = selectedOptionId
    }
}

public struct SettingsImageQualityOption: Identifiable, Equatable {
    public let id: String
    public let label: String
    public let onSelect: () -> Void

    public init(id: String, label: String, onSelect: @escaping () -> Void) {
        self.id = id
        self.label = label
        self.onSelect = onSelect
    }

    public static func == (lhs: SettingsImageQualityOption, rhs: SettingsImageQualityOption) -> Bool {
        lhs.id == rhs.id && lhs.label == rhs.label
    }
}

public struct SettingsThemeItem<Theme: ThemeItem>: Equatable {
    public let icon: String
    public let title: String
    public let subtitle: String
    public let themes: [Theme]
    public let selectedTheme: Theme
    public let onThemeSelected: (Theme) -> Void

    public init(
        icon: String,
        title: String,
        subtitle: String,
        themes: [Theme],
        selectedTheme: Theme,
        onThemeSelected: @escaping (Theme) -> Void
    ) {
        self.icon = icon
        self.title = title
        self.subtitle = subtitle
        self.themes = themes
        self.selectedTheme = selectedTheme
        self.onThemeSelected = onThemeSelected
    }

    public static func == (lhs: SettingsThemeItem, rhs: SettingsThemeItem) -> Bool {
        lhs.icon == rhs.icon
            && lhs.title == rhs.title
            && lhs.subtitle == rhs.subtitle
            && lhs.selectedTheme.id == rhs.selectedTheme.id
    }
}
