import DesignSystem
import SwiftUI

public struct ScanlineOverlay: View {
    let color: Color
    let lineHeight: CGFloat
    let opacity: Double

    public init(
        color: Color,
        lineHeight: CGFloat = 2,
        opacity: Double = 0.15
    ) {
        self.color = color
        self.lineHeight = lineHeight
        self.opacity = opacity
    }

    public var body: some View {
        GeometryReader { _ in
            Canvas { context, size in
                let lineSpacing = lineHeight * 2
                var y: CGFloat = 0

                while y < size.height {
                    let rect = CGRect(x: 0, y: y, width: size.width, height: lineHeight)
                    context.fill(Path(rect), with: .color(color.opacity(opacity)))
                    y += lineSpacing
                }
            }
        }
        .allowsHitTesting(false)
    }
}

public extension View {
    @ViewBuilder
    func scanlineEffect(
        enabled: Bool,
        color: Color,
        lineHeight: CGFloat = 2,
        opacity: Double = 0.15
    ) -> some View {
        if enabled {
            overlay(
                ScanlineOverlay(color: color, lineHeight: lineHeight, opacity: opacity)
            )
        } else {
            self
        }
    }
}

#Preview {
    let theme = TerminalTheme()
    ZStack {
        theme.colors.background
        VStack(spacing: 20) {
            Text("CRT Scanline Effect")
                .textStyle(theme.typography.headlineSmall)
                .foregroundStyle(theme.colors.accent)

            Text("Retro terminal aesthetic")
                .foregroundStyle(theme.colors.accent.opacity(0.8))
        }
    }
    .ignoresSafeArea(.all)
    .scanlineEffect(enabled: true, color: theme.colors.accent, opacity: 0.15)
    .appPreview(theme)
}
