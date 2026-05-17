import SwiftUI

public struct ScanlineConfiguration {
    public let enabled: Bool
    public let color: Color
    public let lineHeight: CGFloat
    public let opacity: Double

    public init(
        enabled: Bool,
        color: Color,
        lineHeight: CGFloat = 2,
        opacity: Double = 0.15
    ) {
        self.enabled = enabled
        self.color = color
        self.lineHeight = lineHeight
        self.opacity = opacity
    }

    public static let disabled = ScanlineConfiguration(enabled: false, color: .clear)

    public static func terminal() -> ScanlineConfiguration {
        ScanlineConfiguration(enabled: true, color: ScanlineColors.terminal, opacity: 0.12)
    }

    public static func amber() -> ScanlineConfiguration {
        ScanlineConfiguration(enabled: true, color: ScanlineColors.amber, opacity: 0.12)
    }

    public static func snow() -> ScanlineConfiguration {
        ScanlineConfiguration(enabled: true, color: ScanlineColors.snow, opacity: 0.08)
    }

    public static func crimson() -> ScanlineConfiguration {
        ScanlineConfiguration(enabled: true, color: ScanlineColors.crimson, opacity: 0.12)
    }
}
