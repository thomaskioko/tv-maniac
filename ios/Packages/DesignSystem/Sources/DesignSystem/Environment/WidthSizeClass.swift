import SwiftUI

/// Three-level width size class mirroring Material3's `WindowWidthSizeClass`, derived from the
/// available width so iOS and Android resolve the same breakpoint at the same width. SwiftUI's
/// built-in `horizontalSizeClass` only distinguishes compact/regular, which is too coarse.
public enum WidthSizeClass: Sendable {
    case compact
    case medium
    case expanded

    public static func from(width: CGFloat) -> WidthSizeClass {
        switch width {
        case ..<600: .compact
        case ..<840: .medium
        default: .expanded
        }
    }
}

public extension EnvironmentValues {
    @Entry var widthSizeClass: WidthSizeClass = .compact
}

public extension View {
    /// Measures the available width at this point in the hierarchy and publishes the derived
    /// ``WidthSizeClass`` to the environment. Apply once near the root.
    func provideWidthSizeClass() -> some View {
        modifier(WidthSizeClassProvider())
    }
}

private struct WidthSizeClassProvider: ViewModifier {
    func body(content: Content) -> some View {
        GeometryReader { proxy in
            content
                .environment(\.widthSizeClass, WidthSizeClass.from(width: proxy.size.width))
        }
    }
}
