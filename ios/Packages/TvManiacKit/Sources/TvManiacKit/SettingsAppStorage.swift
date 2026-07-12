import Components
import DesignSystem
import SwiftUI
import TvManiac

public final class SettingsAppStorage: ObservableObject {
    private init() {}

    public static let shared = SettingsAppStorage()

    @AppStorage("app.theme") public var appTheme: DeviceAppTheme = .system
    @AppStorage("discover.carousel.index") public var savedIndex = 1
    @AppStorage("image.quality") public var imageQuality: SwiftImageQuality = .auto
    @AppStorage("haptic.feedback.enabled") public var hapticFeedbackEnabled = true
    @AppStorage("font.size.percent") public var fontSizePercent = 100
    @AppStorage("poster.width.scale") public var posterWidthScale: Double = 1
    @AppStorage("landscape.width.scale") public var landscapeWidthScale: Double = 1
    @AppStorage("poster.corner.radius") public var posterCornerRadius: Double = 0
}
