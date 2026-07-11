import SwiftUI

public extension EnvironmentValues {
    @Entry var blurImage: Bool = false
}

public extension View {
    func blurImage(_ enabled: Bool) -> some View {
        environment(\.blurImage, enabled)
    }

    func blurEffect() -> some View {
        modifier(BlurEffectModifier())
    }
}

private struct BlurEffectModifier: ViewModifier {
    @Environment(\.blurImage) private var blurEnabled

    private static let blurRadius: CGFloat = 16

    func body(content: Content) -> some View {
        content.blur(radius: blurEnabled ? Self.blurRadius : 0)
    }
}
