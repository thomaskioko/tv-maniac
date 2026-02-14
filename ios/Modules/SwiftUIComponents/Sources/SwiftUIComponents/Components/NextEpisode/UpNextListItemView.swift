import SwiftUI

public struct UpNextListItemView: View {
    @Theme private var theme

    let episode: SwiftNextEpisode
    let onItemClicked: (Int64, Int64) -> Void
    let onShowTitleClicked: (Int64) -> Void
    let onMarkWatched: () -> Void

    // Cache computed values to avoid recalculation
    private let posterImageUrl: String?
    private let episodeInfoText: String

    public init(
        episode: SwiftNextEpisode,
        onItemClicked: @escaping (Int64, Int64) -> Void,
        onShowTitleClicked: @escaping (Int64) -> Void,
        onMarkWatched: @escaping () -> Void
    ) {
        self.episode = episode
        self.onItemClicked = onItemClicked
        self.onShowTitleClicked = onShowTitleClicked
        self.onMarkWatched = onMarkWatched

        posterImageUrl = episode.imageUrl

        var text = episode.episodeNumber
        if episode.remainingEpisodes > 0 {
            text += " +\(episode.remainingEpisodes)"
        }
        if let runtime = episode.runtime {
            text += " (\(runtime))"
        }
        episodeInfoText = text
    }

    public var body: some View {
        Button(action: {
            onItemClicked(episode.showTraktId, episode.episodeId)
        }) {
            HStack(alignment: .top, spacing: 0) {
                posterView
                episodeDetails
                watchedButton
            }
            .frame(height: UpNextListItemViewConstants.height)
            .frame(maxWidth: .infinity)
            .background(theme.colors.surface)
            .cornerRadius(UpNextListItemViewConstants.cornerRadius)
        }
        .buttonStyle(PlainButtonStyle())
        .padding(.horizontal, theme.spacing.xSmall)
    }

    private var posterView: some View {
        PosterItemView(
            title: nil,
            posterUrl: posterImageUrl,
            posterWidth: UpNextListItemViewConstants.imageWidth,
            posterHeight: UpNextListItemViewConstants.height,
            posterRadius: 0
        )
        .frame(width: UpNextListItemViewConstants.imageWidth, height: UpNextListItemViewConstants.height)
        .clipped()
    }

    private var episodeDetails: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            TextTitlePill(
                title: episode.showName,
                onTap: { onShowTitleClicked(episode.showTraktId) }
            )

            Text(episodeInfoText)
                .textStyle(theme.typography.bodySmall)
                .foregroundColor(theme.colors.accent)
                .lineLimit(1)
                .padding(.top, 8)

            Text(episode.episodeTitle)
                .textStyle(theme.typography.bodySmall)
                .foregroundColor(theme.colors.onSurface.opacity(0.7))
                .lineLimit(2)

            Spacer()

            progressView
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, theme.spacing.small)
        .padding(.horizontal, theme.spacing.xSmall)
    }

    private var progressView: some View {
        let progress: Float = episode.totalCount > 0
            ? Float(episode.watchedCount) / Float(episode.totalCount)
            : 0

        return HStack(spacing: 8) {
            SegmentedProgressBar(
                segmentProgress: [progress],
                height: 4
            )
            .frame(maxWidth: .infinity)

            if episode.totalCount > 0 {
                Text("\(episode.watchedCount)/\(episode.totalCount)")
                    .textStyle(theme.typography.labelSmall)
                    .foregroundColor(theme.colors.onSurface.opacity(0.6))
            }
        }
    }

    private var watchedButton: some View {
        Button(action: onMarkWatched) {
            ZStack {
                Circle()
                    .fill(theme.colors.grey)
                    .frame(width: UpNextListItemViewConstants.checkmarkSize, height: UpNextListItemViewConstants.checkmarkSize)
                Image(systemName: "checkmark")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(.white)
            }
        }
        .buttonStyle(.plain)
        .frame(maxHeight: .infinity)
        .padding(.trailing, theme.spacing.medium)
    }
}

private enum UpNextListItemViewConstants {
    static let height: CGFloat = 140
    static let imageWidth: CGFloat = 120
    static let cornerRadius: CGFloat = 2
    static let checkmarkSize: CGFloat = 32
}

#Preview {
    VStack {
        UpNextListItemView(
            episode: SwiftNextEpisode(
                showTraktId: 1,
                showName: "The Walking Dead: Daryl Dixon",
                imageUrl: "/still.jpg",
                episodeId: 123,
                episodeTitle: "L'ame Perdue",
                episodeNumber: "S02 | E01",
                seasonId: 1,
                seasonNumber: 2,
                episodeNumberValue: 1,
                runtime: "45 min",
                overview: "Daryl washes ashore in France.",
                badge: .premiere,
                remainingEpisodes: 7,
                watchedCount: 3,
                totalCount: 10
            ),
            onItemClicked: { _, _ in },
            onShowTitleClicked: { _ in },
            onMarkWatched: {}
        )
    }
    .themedPreview()
}
