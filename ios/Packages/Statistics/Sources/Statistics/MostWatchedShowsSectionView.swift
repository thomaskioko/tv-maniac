import Components
import DesignSystem
import SwiftUI
import TvManiac

struct MostWatchedShowsSectionView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass

    let shows: [SwiftMostWatchedShowItem]
    let title: String
    let onShowClick: (Int64) -> Void

    private var posterWidth: CGFloat {
        ImageType.poster.width(widthSizeClass)
    }

    var body: some View {
        CollapsibleSection(title: title) {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(alignment: .top, spacing: theme.spacing.small) {
                    ForEach(shows) { show in
                        Button(action: { onShowClick(show.showId) }) {
                            card(show)
                        }
                        .buttonStyle(.plain)
                        .testTag(StatisticsTestTags.shared.mostWatchedShowCard(showId: show.showId))
                    }
                }
                .padding(.horizontal, theme.spacing.medium)
            }
            .screenTag(StatisticsTestTags.shared.MOST_WATCHED_ROW_TEST_TAG)
        }
    }

    private func card(_ show: SwiftMostWatchedShowItem) -> some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            PosterItemView(
                title: show.title,
                posterUrl: show.posterPath,
                posterWidth: posterWidth,
                aspectRatio: ImageType.poster.aspect,
                posterRadius: theme.shapes.medium
            )

            Text(show.title)
                .textStyle(theme.typography.titleSmall)
                .foregroundStyle(theme.colors.onSurface)
                .lineLimit(1)

            Text(show.caption)
                .textStyle(theme.typography.bodySmall)
                .foregroundStyle(theme.colors.onSurfaceVariant)
                .lineLimit(1)
        }
        .frame(width: posterWidth, alignment: .leading)
    }
}

#Preview {
    MostWatchedShowsSectionView(
        shows: [
            .init(showId: 1396, title: "Breaking Bad", posterPath: nil, episodeCount: 62, caption: "62 episodes"),
            .init(showId: 1399, title: "Game of Thrones", posterPath: nil, episodeCount: 73, caption: "73 episodes"),
        ],
        title: "Most watched shows",
        onShowClick: { _ in }
    )
}
