import Foundation

public struct SettingsToggleItem: Identifiable {
    public let id: String
    public let icon: String
    public let title: String
    public let subtitle: String
    public let secondarySubtitle: String?
    public let isOn: Bool
    public let isLocked: Bool
    public let lockedBadgeText: String
    public let lockedAccessibilityLabel: String
    public let onToggle: (Bool) -> Void

    public init(
        id: String,
        icon: String,
        title: String,
        subtitle: String,
        secondarySubtitle: String? = nil,
        isOn: Bool,
        isLocked: Bool = false,
        lockedBadgeText: String = "",
        lockedAccessibilityLabel: String = "",
        onToggle: @escaping (Bool) -> Void
    ) {
        self.id = id
        self.icon = icon
        self.title = title
        self.subtitle = subtitle
        self.secondarySubtitle = secondarySubtitle
        self.isOn = isOn
        self.isLocked = isLocked
        self.lockedBadgeText = lockedBadgeText
        self.lockedAccessibilityLabel = lockedAccessibilityLabel
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
            && lhs.isLocked == rhs.isLocked
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
