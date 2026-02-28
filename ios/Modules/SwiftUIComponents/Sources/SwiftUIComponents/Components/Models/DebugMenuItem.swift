import Foundation

public enum DebugMenuItemRole: Equatable {
    case accent
    case destructive
}

public struct DebugMenuItem: Identifiable {
    public let id: String
    public let icon: String
    public let role: DebugMenuItemRole
    public let title: String
    public let subtitle: String
    public let isLoading: Bool
    public let isEnabled: Bool
    public let onTap: () -> Void

    public init(
        id: String,
        icon: String,
        role: DebugMenuItemRole = .accent,
        title: String,
        subtitle: String,
        isLoading: Bool = false,
        isEnabled: Bool = true,
        onTap: @escaping () -> Void
    ) {
        self.id = id
        self.icon = icon
        self.role = role
        self.title = title
        self.subtitle = subtitle
        self.isLoading = isLoading
        self.isEnabled = isEnabled
        self.onTap = onTap
    }
}

extension DebugMenuItem: Equatable {
    public static func == (lhs: DebugMenuItem, rhs: DebugMenuItem) -> Bool {
        lhs.id == rhs.id
            && lhs.icon == rhs.icon
            && lhs.role == rhs.role
            && lhs.title == rhs.title
            && lhs.subtitle == rhs.subtitle
            && lhs.isLoading == rhs.isLoading
            && lhs.isEnabled == rhs.isEnabled
    }
}
