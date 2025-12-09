import SwiftUI
import SwiftUIComponents
import TvManiac

struct AppThemeModifier: ViewModifier {
    @Environment(\.colorScheme) private var colorScheme
    @ObservedObject private var store = SettingsAppStorage.shared

    private var appTheme: DeviceAppTheme { store.appTheme }

    private var resolvedTheme: TvManiacTheme {
        appTheme.designSystemTheme
    }

    func body(content: Content) -> some View {
        content
            .environment(\.colorScheme, appTheme.overrideTheme ?? colorScheme)
            .environment(\.tvManiacTheme, resolvedTheme)
            .overlay(
                ScanlineOverlay(
                    color: resolvedTheme.scanlineConfig.color,
                    lineHeight: resolvedTheme.scanlineConfig.lineHeight,
                    opacity: resolvedTheme.scanlineConfig.opacity
                )
                .opacity(resolvedTheme.scanlineConfig.enabled ? 1 : 0)
                .allowsHitTesting(false)
                .ignoresSafeArea()
            )
    }
}

struct AppTintModifier: ViewModifier {
    @ObservedObject private var store = SettingsAppStorage.shared

    func body(content: Content) -> some View {
        content
            .tint(store.appTheme.toAppThemeColor())
    }
}
