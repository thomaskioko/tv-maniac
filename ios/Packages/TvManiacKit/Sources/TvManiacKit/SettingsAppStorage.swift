import Components
import DesignSystem
import SwiftUI
import TvManiac

public final class SettingsAppStorage: ObservableObject {
    private init() {}

    public static let shared = SettingsAppStorage()

    @AppStorage(StorageKey.appTheme) public var appTheme: DeviceAppTheme = .system
    @AppStorage(StorageKey.carouselIndex) public var savedIndex = 1
    @AppStorage(StorageKey.imageQuality) public var imageQuality: SwiftImageQuality = .auto
    @AppStorage(StorageKey.hapticFeedbackEnabled) public var hapticFeedbackEnabled = true
    @AppStorage(StorageKey.fontSizePercent) public var fontSizePercent = 100
    @AppStorage(StorageKey.posterWidthScale) public var posterWidthScale: Double = 1
    @AppStorage(StorageKey.landscapeWidthScale) public var landscapeWidthScale: Double = 1
    @AppStorage(StorageKey.posterCornerRadius) public var posterCornerRadius: Double = 0
}

public enum StorageKey {
    public static let appTheme = "app.theme"
    public static let carouselIndex = "discover.carousel.index"
    public static let imageQuality = "image.quality"
    public static let hapticFeedbackEnabled = "haptic.feedback.enabled"
    public static let fontSizePercent = "font.size.percent"
    public static let posterWidthScale = "poster.width.scale"
    public static let landscapeWidthScale = "landscape.width.scale"
    public static let posterCornerRadius = "poster.corner.radius"

    static let all = [
        appTheme,
        carouselIndex,
        imageQuality,
        hapticFeedbackEnabled,
        fontSizePercent,
        posterWidthScale,
        landscapeWidthScale,
        posterCornerRadius,
    ]
}

public extension SettingsAppStorage {
    private static let clearStateEnvironmentKey = "TVMANIAC_CLEAR_STATE"

    static func clearStoredValuesIfRequested() {
        guard ProcessInfo.processInfo.environment[clearStateEnvironmentKey] == "1" else { return }

        let defaults = UserDefaults.standard
        for key in StorageKey.all {
            defaults.removeObject(forKey: key)
        }
    }
}
