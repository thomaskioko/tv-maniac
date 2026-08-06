import DesignSystem
import SwiftUI
import TvManiac

private let tileGridColumns = 2

struct StatisticTileGridView: View {
    @Environment(\.appTheme) private var theme

    let tiles: [SwiftStatisticTile]

    private var rows: [[SwiftStatisticTile]] {
        stride(from: 0, to: tiles.count, by: tileGridColumns).map { start in
            Array(tiles[start ..< min(start + tileGridColumns, tiles.count)])
        }
    }

    var body: some View {
        VStack(spacing: theme.spacing.small) {
            ForEach(Array(rows.enumerated()), id: \.offset) { _, row in
                HStack(spacing: theme.spacing.small) {
                    ForEach(row) { tile in
                        StatisticTileCardView(tile: tile)
                            .testTag(StatisticsTestTags.shared.tile(id: tile.id))
                    }

                    if row.count < tileGridColumns {
                        Spacer()
                            .frame(maxWidth: .infinity)
                    }
                }
                .fixedSize(horizontal: false, vertical: true)
            }
        }
        .padding(.horizontal, theme.spacing.medium)
        .testTag(StatisticsTestTags.shared.TILE_GRID_TEST_TAG)
    }
}

private struct StatisticTileCardView: View {
    @Environment(\.appTheme) private var theme

    let tile: SwiftStatisticTile

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(tile.label)
                .textStyle(theme.typography.labelSmall)
                .tracking(sectionLabelLetterSpacing)
                .foregroundStyle(theme.colors.onSurfaceVariant)
                .lineLimit(1)

            Spacer().frame(height: theme.spacing.xxSmall)

            Text(tile.value)
                .textStyle(theme.typography.headlineMedium)
                .fontWeight(.bold)
                .foregroundStyle(theme.colors.accent)
                .lineLimit(1)

            if !tile.caption.isEmpty {
                Spacer().frame(height: theme.spacing.xxxSmall)

                Text(tile.caption)
                    .textStyle(theme.typography.bodySmall)
                    .foregroundStyle(theme.colors.onSurfaceVariant)
                    .lineLimit(2)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .padding(theme.spacing.medium)
        .background(theme.colors.surface)
        .clipShape(RoundedRectangle(cornerRadius: theme.shapes.large))
        .appShadow(theme.shadows.small)
    }
}

#Preview {
    StatisticTileGridView(tiles: [
        .init(id: "TitlesTracked", label: "Titles tracked", value: "128", caption: "18 completed"),
        .init(id: "Episodes", label: "Episodes", value: "1.2K", caption: ""),
        .init(id: "AverageRating", label: "Average rating", value: "8.4", caption: "across 42 rated"),
        .init(id: "WatchStreak", label: "Watch streak", value: "9 days", caption: "longest run"),
    ])
}
