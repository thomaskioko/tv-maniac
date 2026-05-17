import SwiftUI

public struct AppDisplayStyle: ViewModifier {
    @Environment(\.appTheme) private var theme

    public func body(content: Content) -> some View {
        content.font(theme.typography.displayLarge)
    }
}

public struct AppHeadlineStyle: ViewModifier {
    @Environment(\.appTheme) private var theme

    public func body(content: Content) -> some View {
        content.font(theme.typography.headlineMedium)
    }
}

public struct AppTitleStyle: ViewModifier {
    @Environment(\.appTheme) private var theme

    public func body(content: Content) -> some View {
        content.font(theme.typography.titleMedium)
    }
}

public struct AppBodyStyle: ViewModifier {
    @Environment(\.appTheme) private var theme

    public func body(content: Content) -> some View {
        content.font(theme.typography.bodyMedium)
    }
}

public struct AppCaptionStyle: ViewModifier {
    @Environment(\.appTheme) private var theme

    public func body(content: Content) -> some View {
        content
            .font(theme.typography.labelSmall)
            .foregroundStyle(.secondary)
    }
}

public struct AppLabelStyle: ViewModifier {
    @Environment(\.appTheme) private var theme

    public func body(content: Content) -> some View {
        content.font(theme.typography.labelMedium)
    }
}

public extension View {
    func appDisplay() -> some View {
        modifier(AppDisplayStyle())
    }

    func appHeadline() -> some View {
        modifier(AppHeadlineStyle())
    }

    func appTitle() -> some View {
        modifier(AppTitleStyle())
    }

    func appBody() -> some View {
        modifier(AppBodyStyle())
    }

    func appCaption() -> some View {
        modifier(AppCaptionStyle())
    }

    func appLabel() -> some View {
        modifier(AppLabelStyle())
    }
}
