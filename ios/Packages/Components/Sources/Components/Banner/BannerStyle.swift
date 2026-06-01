import SwiftUI

/// Visual variant for ``TvManiacBanner``. Owns its own color palette so it can evolve
/// independently from toast / snackbar styling.
public enum BannerStyle {
    case error
    case warning
    case success
    case info
}

public extension BannerStyle {
    var backgroundColor: Color {
        switch self {
        case .error: Color.red
        case .warning: Color.orange
        case .info: Color.blue
        case .success: Color.green
        }
    }
}
