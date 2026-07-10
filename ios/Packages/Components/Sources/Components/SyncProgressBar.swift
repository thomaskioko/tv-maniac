import DesignSystem
import SwiftUI

/// Slim, non-interactive sync indicator pinned to the top edge of the window.
///
/// Renders a thin indeterminate bar while a background sync runs. Disables hit testing so taps pass
/// through to the toolbar beneath it.
public struct SyncProgressBar: View {
    @Environment(\.appTheme) private var theme
    @State private var animating = false

    private let height: CGFloat = 4

    public init() {}

    public var body: some View {
        GeometryReader { proxy in
            let width = proxy.size.width
            let segment = width * 0.35

            ZStack(alignment: .leading) {
                Rectangle()
                    .fill(theme.colors.secondary.opacity(0.25))

                Rectangle()
                    .fill(theme.colors.secondary)
                    .frame(width: segment)
                    .offset(x: animating ? width : -segment)
                    .animation(
                        .easeInOut(duration: 1.1).repeatForever(autoreverses: false),
                        value: animating
                    )
            }
            .frame(height: height)
            .clipped()
        }
        .frame(height: height)
        .allowsHitTesting(false)
        .onAppear { animating = true }
    }
}

#Preview {
    VStack {
        SyncProgressBar()
        Spacer()
    }
}
