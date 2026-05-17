import SwiftUI

public struct AppScreenStyle: ViewModifier {
    public func body(content: Content) -> some View {
        content
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .foregroundStyle(.appOnBackground)
            .background(.appBackground)
    }
}

public struct AppCardStyle: ViewModifier {
    @Environment(\.appTheme) private var theme

    public func body(content: Content) -> some View {
        content
            .foregroundStyle(.appOnSurface)
            .padding(theme.spacing.medium)
            .background(
                .appSurface,
                in: RoundedRectangle(cornerRadius: theme.shapes.medium)
            )
    }
}

public struct AppSurfaceStyle: ViewModifier {
    public func body(content: Content) -> some View {
        content
            .foregroundStyle(.appOnSurface)
            .background(.appSurface)
    }
}

public extension View {
    func appScreen() -> some View {
        modifier(AppScreenStyle())
    }

    func appCard() -> some View {
        modifier(AppCardStyle())
    }

    func appSurface() -> some View {
        modifier(AppSurfaceStyle())
    }
}
