import DesignSystem
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
    func backgroundColor(theme: TvManiacTheme) -> Color {
        switch self {
        case .error: theme.colors.error
        case .warning: Color.orange
        case .info: Color.blue
        case .success: Color.green
        }
    }
}
