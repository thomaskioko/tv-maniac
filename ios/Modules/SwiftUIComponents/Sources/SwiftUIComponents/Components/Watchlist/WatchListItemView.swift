import SwiftUI

public struct WatchListItemView: View {
    @Theme private var theme

    let episode: SwiftNextEpisode
    let premiereLabel: String
    let newLabel: String
    let onItemClicked: (Int64, Int64) -> Void
    let onShowTitleClicked: (Int64) -> Void
    let onMarkWatched: () -> Void

    public init(
        episode: SwiftNextEpisode,
        premiereLabel: String,
        newLabel: String,
        onItemClicked: @escaping (Int64, Int64) -> Void,
        onShowTitleClicked: @escaping (Int64) -> Void,
        onMarkWatched: @escaping () -> Void
    ) {
        self.episode = episode
        self.premiereLabel = premiereLabel
        self.newLabel = newLabel
        self.onItemClicked = onItemClicked
        self.onShowTitleClicked = onShowTitleClicked
        self.onMarkWatched = onMarkWatched
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
            .frame(height: WatchListItemViewConstants.height)
            .frame(maxWidth: .infinity)
            .background(theme.colors.surface)
            .cornerRadius(WatchListItemViewConstants.cornerRadius)
        }
        .buttonStyle(PlainButtonStyle())
        .padding(.horizontal, theme.spacing.medium)
    }

    private var posterView: some View {
        PosterItemView(
            title: nil,
            posterUrl: episode.imageUrl,
            posterWidth: WatchListItemViewConstants.imageWidth,
            posterHeight: WatchListItemViewConstants.height,
            posterRadius: 0
        )
        .frame(width: WatchListItemViewConstants.imageWidth, height: WatchListItemViewConstants.height)
        .clipped()
    }

    private var episodeDetails: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            TextTitlePill(
                title: episode.showName,
                onTap: { onShowTitleClicked(episode.showTraktId) }
            )

            HStack(spacing: 4) {
                Text(episode.episodeNumber)
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(theme.colors.onSurface)
                    .lineLimit(1)
                if episode.remainingEpisodes > 0 {
                    Text("+\(episode.remainingEpisodes)")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(theme.colors.onSurface.opacity(0.6))
                }
            }
            .padding(.top, 16)

            Text(episode.episodeTitle)
                .font(.system(size: 12))
                .foregroundColor(theme.colors.onSurface.opacity(0.7))
                .lineLimit(2)
                .padding(.top, 4)

            Spacer()

            badgeView
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, theme.spacing.small)
        .padding(.horizontal, theme.spacing.xSmall)
    }

    private var badgeView: some View {
        HStack(spacing: 4) {
            switch episode.badge {
            case .premiere:
                premiereBadge
            case .new:
                newBadge
            case .none:
                EmptyView()
            }
        }
    }

    private var premiereBadge: some View {
        Text(premiereLabel)
            .font(.system(size: 10, weight: .medium))
            .foregroundColor(theme.colors.background)
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(
                RoundedRectangle(cornerRadius: 4)
                    .fill(theme.colors.onSurface)
            )
    }

    private var newBadge: some View {
        Text(newLabel)
            .font(.system(size: 10, weight: .medium))
            .foregroundColor(theme.colors.onSecondary)
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(
                RoundedRectangle(cornerRadius: 4)
                    .fill(theme.colors.secondary)
            )
    }

    private var watchedButton: some View {
        Button(action: onMarkWatched) {
            ZStack {
                Circle()
                    .fill(theme.colors.grey)
                    .frame(width: WatchListItemViewConstants.checkmarkSize, height: WatchListItemViewConstants.checkmarkSize)
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

private enum WatchListItemViewConstants {
    static let height: CGFloat = 140
    static let imageWidth: CGFloat = 120
    static let cornerRadius: CGFloat = 2
    static let checkmarkSize: CGFloat = 32
}

#Preview {
    VStack {
        WatchListItemView(
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
                remainingEpisodes: 7
            ),
            premiereLabel: "PREMIERE",
            newLabel: "NEW",
            onItemClicked: { _, _ in },
            onShowTitleClicked: { _ in },
            onMarkWatched: {}
        )
    }
    .themedPreview()
}
