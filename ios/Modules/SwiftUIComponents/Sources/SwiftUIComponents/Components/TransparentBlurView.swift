import SwiftUI

public struct TransparentBlurView: UIViewRepresentable {
    private let style: UIBlurEffect.Style

    public init(style: UIBlurEffect.Style) {
        self.style = style
    }

    public func makeUIView(context _: Context) -> UIVisualEffectView {
        UIVisualEffectView(effect: UIBlurEffect(style: style))
    }

    public func updateUIView(_ uiView: UIVisualEffectView, context _: Context) {
        uiView.effect = UIBlurEffect(style: style)
    }
}
