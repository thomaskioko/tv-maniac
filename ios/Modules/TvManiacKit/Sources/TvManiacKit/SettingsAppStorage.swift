import SwiftUI
import TvManiac

public final class SettingsAppStorage: ObservableObject {
    private init() {}

    public static let shared = SettingsAppStorage()

    @AppStorage("app.theme") public var appTheme: DeviceAppTheme = .light
    @AppStorage("discover.carousel.index") public var savedIndex = 1
    @AppStorage("image.quality") public var imageQuality: SwiftImageQuality = .medium
}
