import SwiftUI

public struct UpNextListItemView: View {
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
            onItemClicked(episode.showId, episode.episodeId)
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
        .padding(.horizontal, theme.spacing.medium)
    }

    private var posterView: some View {
        PosterItemView(
            title: nil,
            posterUrl: episode.stillImage ?? episode.showPoster,
            posterWidth: UpNextListItemViewConstants.imageWidth,
            posterHeight: UpNextListItemViewConstants.height,
            posterRadius: 0
        )
        .frame(width: UpNextListItemViewConstants.imageWidth, height: UpNextListItemViewConstants.height)
        .clipped()
    }

    private var episodeDetails: some View {
        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
            showTitlePill

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

    private var showTitlePill: some View {
        Button(action: {
            onShowTitleClicked(episode.showId)
        }) {
            HStack(spacing: 2) {
                Text(episode.showName)
                    .font(.system(size: 12, weight: .medium))
                    .lineLimit(1)
                    .foregroundColor(theme.colors.onSurface)
                Image(systemName: "chevron.right")
                    .font(.system(size: 10))
                    .foregroundColor(theme.colors.onSurface)
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(theme.colors.onSurface, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
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
                showId: 1,
                showName: "The Walking Dead: Daryl Dixon",
                showPoster: "/poster.jpg",
                episodeId: 123,
                episodeTitle: "L'ame Perdue",
                episodeNumber: "S02 | E01",
                seasonId: 1,
                seasonNumber: 2,
                episodeNumberValue: 1,
                runtime: "45 min",
                stillImage: "/still.jpg",
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
