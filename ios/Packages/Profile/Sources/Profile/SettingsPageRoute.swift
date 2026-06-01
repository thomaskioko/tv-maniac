import Components
import Foundation
import Models

/// Swift-side mirror of the shared `SettingsPage` enum, used to drive the local
/// settings `NavigationStack`. The integration layer maps the Kotlin `SettingsPage`
/// to this so the presentational components never touch the shared type directly.
public enum SettingsPageRoute: String, Hashable, Identifiable, CaseIterable {
    case root
    case appearance
    case behavior
    case notifications
    case privacy
    case info
    case licenses
    case trakt

    public var id: String {
        rawValue
    }

    /// SF Symbol used for this page's row icon.
    public var iconName: String {
        switch self {
        case .root: "gearshape"
        case .appearance: "paintpalette"
        case .behavior: "slider.horizontal.3"
        case .notifications: "bell.fill"
        case .privacy: "lock.shield"
        case .info: "info.circle"
        case .licenses: "doc.text"
        case .trakt: "person.fill"
        }
    }
}
