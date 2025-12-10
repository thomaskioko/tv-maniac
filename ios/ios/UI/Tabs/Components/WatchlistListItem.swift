import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct WatchlistListItem: View {
    @Theme private var theme

    let item: TvManiac.WatchlistItem
    let namespace: Namespace.ID

    var body: some View {
        HStack(spacing: 0) {
            PosterItemView(
                title: item.title,
                posterUrl: item.posterImageUrl,
                posterWidth: WatchlistListItemConstants.posterWidth,
                posterHeight: WatchlistListItemConstants.height
            )

            watchlistItemDetails(item: item)
        }
        .frame(maxWidth: .infinity)
        .frame(height: WatchlistListItemConstants.height)
        .background(theme.colors.surfaceVariant)
        .cornerRadius(theme.shapes.medium)
        .matchedGeometryEffect(id: item.tmdbId, in: namespace)
    }

    @ViewBuilder
    private func watchlistItemDetails(item: TvManiac.WatchlistItem) -> some View {
        ZStack(alignment: .bottom) {
            VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                Text(item.title)
                    .textStyle(theme.typography.titleMedium)
                    .foregroundColor(theme.colors.onSurface)
                    .lineLimit(1)

                HStack(spacing: theme.spacing.xxSmall) {
                    if item.seasonCount > 0 {
                        Text(String(\.season_count, quantity: Int(item.seasonCount)))
                            .textStyle(theme.typography.bodySmall)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                    }

                    if item.episodeCount > 0 {
                        Text("•")
                            .textStyle(theme.typography.labelSmall)
                            .foregroundColor(theme.colors.onSurfaceVariant)

                        Text(String(\.episode_count, quantity: Int(item.episodeCount)))
                            .textStyle(theme.typography.bodySmall)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                    }
                }

                HStack(spacing: theme.spacing.xxSmall) {
                    if let status = item.status {
                        BorderTextView(
                            text: status,
                            colorOpacity: 0.12,
                            borderOpacity: 0.12,
                            weight: .bold
                        )

                        Text("•")
                            .textStyle(theme.typography.labelSmall)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                    }

                    if let year = item.year {
                        Text("\(year)")
                            .textStyle(theme.typography.bodySmall)
                            .foregroundColor(theme.colors.onSurfaceVariant)
                    }
                }
                .padding(.top, theme.spacing.xxSmall)

                Spacer()
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.vertical)
            .padding(.horizontal, theme.spacing.xSmall)

            ProgressView(value: 0, total: 1)
                .progressViewStyle(RoundedRectProgressViewStyle())
                .offset(y: 2)
        }
    }
}

// Extract item details into separate view

public enum WatchlistListItemConstants {
    static let height: CGFloat = 140
    static let posterWidth: CGFloat = 100
}
