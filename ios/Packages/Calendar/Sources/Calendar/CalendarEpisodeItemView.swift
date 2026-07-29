import Components
import DesignSystem
import SwiftUI

struct CalendarEpisodeItemView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.widthSizeClass) private var widthSizeClass

    let episode: SwiftCalendarEpisodeItem
    let moreEpisodesFormat: (Int32) -> String
    let onEpisodeCardClicked: (Int64) -> Void

    var body: some View {
        Button {
            onEpisodeCardClicked(episode.episodeId)
        } label: {
            VStack(spacing: 0) {
                HStack(spacing: theme.spacing.small) {
                    PosterItemView(
                        title: nil,
                        posterUrl: episode.posterUrl,
                        posterWidth: ImageDimens.posterWidthFixed(widthSizeClass),
                        aspectRatio: ImageType.poster.aspect,
                        posterRadius: 0
                    )

                    VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                        Text(episode.showTitle)
                            .textStyle(theme.typography.titleMedium)
                            .foregroundStyle(.appOnSurface)
                            .lineLimit(1)

                        Text(episode.episodeInfo)
                            .textStyle(theme.typography.bodyMedium)
                            .foregroundStyle(.appOnSurfaceVariant)
                            .lineLimit(1)

                        if let airTime = episode.airTime {
                            let airTimeText = episode.network.map { "\(airTime) on \($0)" } ?? airTime
                            Text(airTimeText)
                                .textStyle(theme.typography.bodySmall)
                                .foregroundStyle(.appOnSurfaceVariant.opacity(0.7))
                        }
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
                    .padding(.vertical, theme.spacing.small)
                    .padding(.trailing, theme.spacing.small)
                }

                if episode.additionalEpisodesCount > 0 {
                    HStack {
                        Text(moreEpisodesFormat(episode.additionalEpisodesCount))
                            .textStyle(theme.typography.labelMedium)
                            .foregroundStyle(.appOnSurfaceVariant)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.horizontal, theme.spacing.medium)
                    .padding(.vertical, theme.spacing.small)
                    .background(.appSurfaceVariant)
                }
            }
            .background(.appSurface)
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
        }
        .buttonStyle(.plain)
    }
}
