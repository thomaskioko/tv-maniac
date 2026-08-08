import Components
import DesignSystem
import SwiftUI
import TvManiac

struct WatchHeatMapSectionView: View {
    @Environment(\.appTheme) private var theme

    let heatMap: SwiftWatchHeatMap
    let title: String

    private static let cellSize: CGFloat = 10
    private static let daysInWeek = 7
    private static let trackAlpha: Double = 0.16
    private static let maxLevel: Double = 4

    var body: some View {
        CollapsibleSection(title: title) {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: theme.spacing.xxxSmall) {
                    ForEach(Array(columns.enumerated()), id: \.offset) { _, column in
                        VStack(spacing: theme.spacing.xxxSmall) {
                            ForEach(Array(column.enumerated()), id: \.offset) { _, level in
                                RoundedRectangle(cornerRadius: theme.shapes.small)
                                    .fill(color(for: level))
                                    .frame(width: Self.cellSize, height: Self.cellSize)
                            }
                        }
                    }
                }
                .padding(.horizontal, theme.spacing.medium)
            }
        }
        .screenTag(StatisticsTestTags.shared.HEAT_MAP_TEST_TAG)
    }

    private var columns: [[Int?]] {
        let padded: [Int?] =
            Array(repeating: nil, count: heatMap.leadingBlankCells) + heatMap.levels

        return stride(from: 0, to: padded.count, by: Self.daysInWeek).map { start in
            Array(padded[start ..< min(start + Self.daysInWeek, padded.count)])
        }
    }

    private func color(for level: Int?) -> Color {
        guard let level else { return .clear }

        return level == 0
            ? theme.colors.onSurfaceVariant.opacity(Self.trackAlpha)
            : theme.colors.accent.opacity(Double(level) / Self.maxLevel)
    }
}

#Preview {
    WatchHeatMapSectionView(
        heatMap: SwiftWatchHeatMap(
            levels: (0 ..< 70).map { $0 % 5 },
            leadingBlankCells: 3
        ),
        title: "Your year of watching"
    )
}
