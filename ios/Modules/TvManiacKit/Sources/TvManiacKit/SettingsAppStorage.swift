import SwiftUI

public final class SettingsAppStorage: ObservableObject {
    private init() {
    }

    public static let shared = SettingsAppStorage()

    @AppStorage("app.theme") public var appTheme: DeveiceAppTheme = .light
    @AppStorage("discover.carousel.index") public var savedIndex = 1
}
