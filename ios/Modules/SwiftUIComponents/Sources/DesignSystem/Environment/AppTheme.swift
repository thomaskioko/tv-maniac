import SwiftUI

public extension EnvironmentValues {
    @Entry var appTheme: TvManiacTheme = LightTheme()
}

public extension View {
    func appTheme(_ theme: TvManiacTheme) -> some View {
        environment(\.appTheme, theme)
    }
}
