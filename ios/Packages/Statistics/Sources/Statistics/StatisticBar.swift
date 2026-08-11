import DesignSystem
import SwiftUI

struct StatisticBar: View {
    @Environment(\.appTheme) private var theme

    let fraction: Float

    private static let barHeight: CGFloat = 8
    private static let trackAlpha: Double = 0.24

    var body: some View {
        GeometryReader { proxy in
            ZStack(alignment: .leading) {
                RoundedRectangle(cornerRadius: theme.shapes.small)
                    .fill(theme.colors.onSurfaceVariant.opacity(Self.trackAlpha))

                RoundedRectangle(cornerRadius: theme.shapes.small)
                    .fill(theme.colors.accent)
                    .frame(width: proxy.size.width * CGFloat(min(max(fraction, 0), 1)))
            }
        }
        .frame(height: Self.barHeight)
    }
}

#Preview {
    StatisticBar(fraction: 0.6)
        .padding()
}
