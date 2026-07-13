import SwiftUI

public extension EnvironmentValues {
    @Entry var blurImage: Bool = false
}

public extension View {
    func blurImage(_ enabled: Bool) -> some View {
        environment(\.blurImage, enabled)
    }

    func blurEffect(isWatched: Bool = false) -> some View {
        modifier(BlurEffectModifier(isWatched: isWatched))
    }
}

private struct BlurEffectModifier: ViewModifier {
    @Environment(\.blurImage) private var blurEnabled

    let isWatched: Bool

    private static let blurRadius: CGFloat = 16

    func body(content: Content) -> some View {
        content.blur(radius: blurEnabled && !isWatched ? Self.blurRadius : 0)
    }
}
