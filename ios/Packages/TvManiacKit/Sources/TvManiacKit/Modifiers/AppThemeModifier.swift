import Components
import DesignSystem
import SwiftUI
import TvManiac

struct AppThemeModifier: ViewModifier {
    @Environment(\.colorScheme) private var colorScheme
    @ObservedObject private var store = SettingsAppStorage.shared

    private var appTheme: DeviceAppTheme {
        store.appTheme
    }

    private var resolvedTheme: TvManiacTheme {
        appTheme.designSystemTheme
    }

    func body(content: Content) -> some View {
        TvManiacTypographyScheme.updateFontScale(percent: store.fontSizePercent)
        ImageDimens.updatePosterStyle(
            posterScale: CGFloat(store.posterWidthScale),
            landscapeScale: CGFloat(store.landscapeWidthScale),
            cornerRadius: CGFloat(store.posterCornerRadius)
        )
        return content
            .environment(\.colorScheme, appTheme.overrideTheme ?? colorScheme)
            .appTheme(resolvedTheme)
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
