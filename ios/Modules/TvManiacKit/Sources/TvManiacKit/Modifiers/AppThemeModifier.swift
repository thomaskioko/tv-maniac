import SwiftUI
import TvManiac

struct AppThemeModifier: ViewModifier {
    @Environment(\.colorScheme) private var colorScheme
    @AppStorage("app.theme") private var appTheme: DeveiceAppTheme = .light

    func body(content: Content) -> some View {
        content
            .environment(\.colorScheme, appTheme.overrideTheme ?? colorScheme)
    }
}

struct AppTintModifier: ViewModifier {
    @AppStorage("app.theme") private var appTheme: DeveiceAppTheme = .light

    func body(content: Content) -> some View {
        content
            .tint(appTheme.toAppThemeColor())
    }
}
