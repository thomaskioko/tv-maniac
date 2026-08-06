import Components
import DesignSystem
import SwiftUI
import TvManiac

struct WatchStatusSectionView: View {
    @Environment(\.appTheme) private var theme

    let items: [SwiftWatchStatusItem]
    let title: String

    var body: some View {
        CollapsibleSection(title: title) {
            VStack(spacing: theme.spacing.small) {
                ForEach(items) { item in
                    row(item)
                        .testTag(StatisticsTestTags.shared.watchStatusRow(id: item.id))
                }
            }
            .padding(.horizontal, theme.spacing.medium)
        }
        .testTag(StatisticsTestTags.shared.WATCH_STATUS_SECTION_TEST_TAG)
    }

    private func row(_ item: SwiftWatchStatusItem) -> some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxxSmall) {
            HStack {
                Text(item.label)
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(theme.colors.onSurface)

                Spacer()

                Text("\(item.showCount)")
                    .textStyle(theme.typography.bodyMedium)
                    .foregroundStyle(theme.colors.onSurfaceVariant)
            }

            StatisticBar(fraction: item.fraction)
        }
    }
}

#Preview {
    WatchStatusSectionView(
        items: [
            .init(id: "Watchlist", label: "Watchlist", showCount: 24, fraction: 0.4),
            .init(id: "Watching", label: "Watching", showCount: 6, fraction: 0.1),
            .init(id: "Completed", label: "Completed", showCount: 28, fraction: 0.46),
            .init(id: "OnHold", label: "On hold", showCount: 2, fraction: 0.03),
            .init(id: "Dropped", label: "Dropped", showCount: 1, fraction: 0.01),
        ],
        title: "Shows by status"
    )
}
