import DesignSystem
import SwiftUI

/// Rounded placeholder that sweeps a highlight across a translucent base while content loads.
/// Falls back to a static base when Reduce Motion is on. Size it via `.frame(...)` from the caller.
public struct ShimmerView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    private let cornerRadius: CGFloat

    @SwiftUI.State private var phase: CGFloat = -1

    public init(cornerRadius: CGFloat = 8) {
        self.cornerRadius = cornerRadius
    }

    public var body: some View {
        let base = theme.colors.onSurface.opacity(0.08)
        let highlight = theme.colors.onSurface.opacity(0.18)

        RoundedRectangle(cornerRadius: cornerRadius)
            .fill(base)
            .overlay {
                GeometryReader { proxy in
                    LinearGradient(
                        colors: [.clear, highlight, .clear],
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                    .frame(width: proxy.size.width)
                    .offset(x: phase * proxy.size.width * 2)
                }
            }
            .clipShape(RoundedRectangle(cornerRadius: cornerRadius))
            .onAppear {
                guard !reduceMotion else { return }
                withAnimation(.linear(duration: 1.2).repeatForever(autoreverses: false)) {
                    phase = 1
                }
            }
    }
}
