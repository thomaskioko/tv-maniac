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
    private static let todayBorderWidth: CGFloat = 1

    var body: some View {
        CollapsibleSection(title: title) {
            VStack(alignment: .trailing, spacing: theme.spacing.small) {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: theme.spacing.xxxSmall) {
                        ForEach(Array(columns.enumerated()), id: \.offset) { columnIndex, column in
                            VStack(spacing: theme.spacing.xxxSmall) {
                                ForEach(Array(column.enumerated()), id: \.offset) { rowIndex, level in
                                    cell(level: level, isToday: isToday(columnIndex, rowIndex))
                                }
                            }
                        }
                    }
                    .padding(.horizontal, theme.spacing.medium)
                    .accessibilityHidden(true)
                }

                legend
                    .padding(.horizontal, theme.spacing.medium)
            }
        }
        .screenTag(StatisticsTestTags.shared.HEAT_MAP_TEST_TAG)
    }

    private func cell(level: Int?, isToday: Bool) -> some View {
        RoundedRectangle(cornerRadius: theme.shapes.small)
            .fill(color(for: level))
            .frame(width: Self.cellSize, height: Self.cellSize)
            .overlay(
                RoundedRectangle(cornerRadius: theme.shapes.small)
                    .stroke(isToday ? theme.colors.onSurface : .clear, lineWidth: Self.todayBorderWidth)
            )
    }

    private var legend: some View {
        HStack(spacing: theme.spacing.xxxSmall) {
            Text("0")
                .textStyle(theme.typography.labelSmall)
                .foregroundStyle(theme.colors.onSurfaceVariant)

            ForEach(0 ... Int(Self.maxLevel), id: \.self) { level in
                RoundedRectangle(cornerRadius: theme.shapes.small)
                    .fill(color(for: level))
                    .frame(width: Self.cellSize, height: Self.cellSize)
            }

            Text("\(heatMap.busiestDayCount)")
                .textStyle(theme.typography.labelSmall)
                .foregroundStyle(theme.colors.onSurfaceVariant)
        }
        .testTag(StatisticsTestTags.shared.HEAT_MAP_LEGEND_TEST_TAG)
    }

    private func isToday(_ columnIndex: Int, _ rowIndex: Int) -> Bool {
        let index = columnIndex * Self.daysInWeek + rowIndex - heatMap.leadingBlankCells
        return index == heatMap.levels.count - 1
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
            leadingBlankCells: 3,
            busiestDayCount: 4
        ),
        title: "Your year of watching"
    )
}
