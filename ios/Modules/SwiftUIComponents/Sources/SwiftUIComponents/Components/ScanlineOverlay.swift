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

public struct ScanlineConfiguration {
    public let enabled: Bool
    public let color: Color
    public let lineHeight: CGFloat
    public let opacity: Double

    public init(
        enabled: Bool,
        color: Color,
        lineHeight: CGFloat = 2,
        opacity: Double = 0.15
    ) {
        self.enabled = enabled
        self.color = color
        self.lineHeight = lineHeight
        self.opacity = opacity
    }

    public static let disabled = ScanlineConfiguration(enabled: false, color: .clear)

    public static func terminal() -> ScanlineConfiguration {
        ScanlineConfiguration(enabled: true, color: Color(hex: "20C020"), opacity: 0.12)
    }

    public static func amber() -> ScanlineConfiguration {
        ScanlineConfiguration(enabled: true, color: Color(hex: "FF8C00"), opacity: 0.12)
    }

    public static func snow() -> ScanlineConfiguration {
        ScanlineConfiguration(enabled: true, color: Color(hex: "FFFFFF"), opacity: 0.08)
    }

    public static func crimson() -> ScanlineConfiguration {
        ScanlineConfiguration(enabled: true, color: Color(hex: "FF4D6A"), opacity: 0.12)
    }
}

#Preview {
    ZStack {
        Color.black
        VStack(spacing: 20) {
            Text("CRT Scanline Effect")
                .font(.title)
                .foregroundColor(.green)

            Text("Retro terminal aesthetic")
                .foregroundColor(.green.opacity(0.8))
        }
    }
    .ignoresSafeArea(.all)
    .scanlineEffect(enabled: true, color: .green, opacity: 0.15)
}
