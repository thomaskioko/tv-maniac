import DesignSystem
import SwiftUI

public enum ParallaxConstants {
    public static let collapsedImageHeight: CGFloat = 120
    public static let profileImageHeight: CGFloat = 350
    public static let showDetailsImageHeight: CGFloat = 500
    public static let seasonDetailsImageHeight: CGFloat = 350

    public static let glassBarTriggerOffset: CGFloat = 150
    public static let opacityDivisor: CGFloat = 200

    public static func glassOpacity(from scrollOffset: CGFloat) -> Double {
        let opacity = -scrollOffset - glassBarTriggerOffset
        let normalized = opacity / opacityDivisor
        return max(0, min(1, normalized))
    }

    public static func glassOpacity(
        from scrollOffset: CGFloat,
        triggerOffset: CGFloat,
        divisor: CGFloat
    ) -> Double {
        let opacity = -scrollOffset - triggerOffset
        let normalized = opacity / divisor
        return max(0, min(1, normalized))
    }
}

public enum AnimationConstants {
    public static let defaultDuration: Double = 0.25
}
