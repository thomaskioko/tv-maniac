import SwiftUI
import SwiftUIComponents
import TvManiac

struct AppThemeModifier: ViewModifier {
    @Environment(\.colorScheme) private var colorScheme
    @AppStorage("app.theme") private var appTheme: DeveiceAppTheme = .light

    private var resolvedTheme: TvManiacTheme {
        let effectiveScheme = appTheme.overrideTheme ?? colorScheme
        return effectiveScheme == .dark ? DarkTheme() : LightTheme()
    }

    func body(content: Content) -> some View {
        content
            .environment(\.colorScheme, appTheme.overrideTheme ?? colorScheme)
            .environment(\.tvManiacTheme, resolvedTheme)
    }
}

struct AppTintModifier: ViewModifier {
    @AppStorage("app.theme") private var appTheme: DeveiceAppTheme = .light

    func body(content: Content) -> some View {
        content
            .tint(appTheme.toAppThemeColor())
    }
}
