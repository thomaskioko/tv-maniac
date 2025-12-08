import SwiftUI

public struct ThemedPreview<Content: View>: View {
    @Environment(\.colorScheme) private var colorScheme
    private let content: Content

    public init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    public var body: some View {
        let theme: TvManiacTheme = colorScheme == .dark ? DarkTheme() : LightTheme()
        content
            .environment(\.tvManiacTheme, theme)
            .background(theme.colors.background)
    }
}

public extension View {
    func themedPreview() -> some View {
        ThemedPreview { self }
    }
}
