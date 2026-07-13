import Components
import DesignSystem
import Models
import SwiftUI

public struct UpNextListItemView: View {
    @Environment(\.appTheme) private var theme
    @Environment(\.hapticFeedbackEnabled) private var hapticFeedbackEnabled

    let episode: SwiftNextEpisode
    let onItemClicked: (Int64, Int64) -> Void
    let onShowTitleClicked: (Int64) -> Void
    let onMarkWatched: () -> Void
    let onLongPress: () -> Void
    let isUpdating: Bool

    private let posterImageUrl: String?
    private let episodeInfoText: String

    public init(
        episode: SwiftNextEpisode,
        onItemClicked: @escaping (Int64, Int64) -> Void,
        onShowTitleClicked: @escaping (Int64) -> Void,
        onMarkWatched: @escaping () -> Void,
        onLongPress: @escaping () -> Void = {},
        isUpdating: Bool = false
    ) {
        self.episode = episode
        self.onItemClicked = onItemClicked
        self.onShowTitleClicked = onShowTitleClicked
        self.onMarkWatched = onMarkWatched
        self.onLongPress = onLongPress
        self.isUpdating = isUpdating

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
        HStack(alignment: .top, spacing: 0) {
            posterView
            episodeDetails
            watchedButton
        }
        .frame(height: UpNextListItemViewConstants.height)
        .frame(maxWidth: .infinity)
        .background(.appSurface)
        .cornerRadius(UpNextListItemViewConstants.cornerRadius)
        .contentShape(Rectangle())
        .onTapGesture {
            onItemClicked(episode.showId, episode.episodeId)
        }
        .onLongPressGesture {
            Haptics.impact(isEnabled: hapticFeedbackEnabled)
            onLongPress()
        }
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
        .blurEffect()
        .clipped()
    }

    private var episodeDetails: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            TextTitlePill(
                title: episode.showName,
                onTap: { onShowTitleClicked(episode.showId) }
            )

            Text(episodeInfoText)
                .textStyle(theme.typography.bodySmall)
                .foregroundStyle(.appAccent)
                .lineLimit(1)
                .padding(.top, theme.spacing.xSmall)

            Text(episode.episodeTitle)
                .textStyle(theme.typography.bodySmall)
                .foregroundStyle(.appOnSurface.opacity(0.7))
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

        return HStack(spacing: theme.spacing.xSmall) {
            SegmentedProgressBar(
                segmentProgress: [progress],
                height: 4
            )
            .frame(maxWidth: .infinity)

            if episode.totalCount > 0 {
                Text("\(episode.watchedCount)/\(episode.totalCount)")
                    .textStyle(theme.typography.labelSmall)
                    .foregroundStyle(.appOnSurface.opacity(0.6))
            }
        }
    }

    private var watchedButton: some View {
        Button(action: {
            Haptics.impact(isEnabled: hapticFeedbackEnabled)
            onMarkWatched()
        }) {
            ZStack {
                Circle()
                    .fill(.appGrey)
                    .frame(
                        width: UpNextListItemViewConstants.checkmarkSize,
                        height: UpNextListItemViewConstants.checkmarkSize
                    )
                if isUpdating {
                    ProgressView()
                        .progressViewStyle(.circular)
                        .tint(theme.colors.onPrimary)
                } else {
                    Image(systemName: "checkmark")
                        .textStyle(theme.typography.titleSmall)
                        .foregroundStyle(.white)
                }
            }
            .frame(width: UpNextListItemViewConstants.tapTargetSize, height: UpNextListItemViewConstants.height)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .disabled(isUpdating)
        .frame(maxHeight: .infinity)
        .padding(.trailing, theme.spacing.small)
    }
}

private enum UpNextListItemViewConstants {
    static let height: CGFloat = 140
    static let imageWidth: CGFloat = 120
    static let cornerRadius: CGFloat = 2
    static let checkmarkSize: CGFloat = 36
    static let tapTargetSize: CGFloat = 48
}

#Preview {
    VStack {
        UpNextListItemView(
            episode: SwiftNextEpisode(
                showId: 1,
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
    .appPreview()
}
