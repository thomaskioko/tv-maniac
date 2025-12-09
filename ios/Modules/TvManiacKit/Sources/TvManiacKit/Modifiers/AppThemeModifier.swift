import SwiftUI
import SwiftUIComponents
import TvManiac

struct AppThemeModifier: ViewModifier {
    @Environment(\.colorScheme) private var colorScheme
    @AppStorage("app.theme") private var appTheme: DeviceAppTheme = .light

    private var resolvedTheme: TvManiacTheme {
        resolveTheme(for: appTheme, colorScheme: colorScheme)
    }

    func body(content: Content) -> some View {
        content
            .environment(\.colorScheme, appTheme.overrideTheme ?? colorScheme)
            .environment(\.tvManiacTheme, resolvedTheme)
            .onAppear {
                configureNavigationBarAppearance()
            }
            .onChange(of: appTheme) { _ in
                configureNavigationBarAppearance()
            }
    }

    private func configureNavigationBarAppearance() {
        let theme = resolveTheme(for: appTheme, colorScheme: UITraitCollection.current.userInterfaceStyle == .dark ? .dark : .light)

        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = UIColor(theme.colors.surface)
        appearance.titleTextAttributes = [
            .foregroundColor: UIColor(theme.colors.onSurface),
        ]
        appearance.largeTitleTextAttributes = [
            .foregroundColor: UIColor(theme.colors.onSurface),
        ]

        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
        UINavigationBar.appearance().compactAppearance = appearance
        UINavigationBar.appearance().tintColor = UIColor(theme.colors.accent)
    }

    private func resolveTheme(for theme: DeviceAppTheme, colorScheme: ColorScheme) -> TvManiacTheme {
        switch theme {
        case .system:
            colorScheme == .dark ? DarkTheme() : LightTheme()
        case .light:
            LightTheme()
        case .dark:
            DarkTheme()
        case .terminal:
            TerminalTheme()
        case .autumn:
            AutumnTheme()
        case .aqua:
            AquaTheme()
        }
    }
}

struct AppTintModifier: ViewModifier {
    @AppStorage("app.theme") private var appTheme: DeviceAppTheme = .light

    func body(content: Content) -> some View {
        content
            .tint(appTheme.toAppThemeColor())
    }
}
