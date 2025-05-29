import SwiftUI

public struct ButtonElevationEffect: ViewModifier {
    @Binding private var isPressed: Bool

    public init(isPressed: Binding<Bool>) {
        _isPressed = isPressed
    }

    public func body(content: Content) -> some View {
        content
            .scaleEffect(isPressed ? 0.9 : 1.0)
            .opacity(isPressed ? 0.8 : 1.0)
            .animation(.easeInOut(duration: 0.2), value: isPressed)
    }
}

extension View {
    func buttonElevationEffect(isPressed: Binding<Bool>) -> some View {
        modifier(ButtonElevationEffect(isPressed: isPressed))
    }
}
