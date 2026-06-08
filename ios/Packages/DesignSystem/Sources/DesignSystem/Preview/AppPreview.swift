import SwiftUI

public struct AppPreviewModifier: ViewModifier {
    @Environment(\.colorScheme) private var colorScheme

    private let explicitTheme: TvManiacTheme?

    public init(explicitTheme: TvManiacTheme?) {
        self.explicitTheme = explicitTheme
    }

    public func body(content: Content) -> some View {
        let theme: TvManiacTheme = explicitTheme ?? (colorScheme == .dark ? DarkTheme() : LightTheme())
        content
            .appTheme(theme)
            .background(theme.colors.background)
    }
}

public extension View {
    func appPreview(_ theme: TvManiacTheme? = nil) -> some View {
        modifier(AppPreviewModifier(explicitTheme: theme))
    }
}
