import SwiftUI

public struct NextEpisodeCard: View {
    @Theme private var theme

    private let episode: SwiftNextEpisode
    private let markWatchedLabel: String
    private let unfollowShowLabel: String
    private let openSeasonLabel: String
    private let onEpisodeClick: (Int64, Int64) -> Void
    private let onMarkWatched: () -> Void
    private let onUnfollowShow: () -> Void
    private let onOpenSeason: () -> Void

    public init(
        episode: SwiftNextEpisode,
        markWatchedLabel: String,
        unfollowShowLabel: String,
        openSeasonLabel: String,
        onEpisodeClick: @escaping (Int64, Int64) -> Void,
        onMarkWatched: @escaping () -> Void = {},
        onUnfollowShow: @escaping () -> Void = {},
        onOpenSeason: @escaping () -> Void = {}
    ) {
        self.episode = episode
        self.markWatchedLabel = markWatchedLabel
        self.unfollowShowLabel = unfollowShowLabel
        self.openSeasonLabel = openSeasonLabel
        self.onEpisodeClick = onEpisodeClick
        self.onMarkWatched = onMarkWatched
        self.onUnfollowShow = onUnfollowShow
        self.onOpenSeason = onOpenSeason
    }

    public var body: some View {
        Button(action: {
            onEpisodeClick(episode.showTraktId, episode.episodeId)
        }) {
            ZStack {
                CachedAsyncImage(
                    url: episode.stillImage ?? episode.showPoster,
                    priority: .high,
                    showIndicator: true
                ) {
                    Rectangle()
                        .fill(Color.gray.opacity(0.3))
                        .overlay(
                            Image(systemName: "tv")
                                .textStyle(theme.typography.titleLarge)
                                .foregroundColor(theme.colors.onSurfaceVariant)
                        )
                }
                .scaledToFill()
                .frame(width: DimensionConstants.imageWidth, height: DimensionConstants.imageHeight)
                .clipped()

                LinearGradient(
                    gradient: Gradient(colors: [
                        Color.clear,
                        Color.black.opacity(0.7),
                    ]),
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(width: DimensionConstants.imageWidth, height: DimensionConstants.imageHeight)

                if let runtime = episode.runtime {
                    VStack {
                        HStack {
                            Spacer()
                            Text(runtime)
                                .textStyle(theme.typography.labelSmall)
                                .foregroundColor(theme.colors.onPrimary)
                                .padding(.horizontal, theme.spacing.xxSmall + 2)
                                .padding(.vertical, theme.spacing.xxxSmall)
                                .background(Color.black.opacity(0.6))
                                .cornerRadius(theme.shapes.small)
                                .padding(theme.spacing.xSmall)
                        }
                        Spacer()
                    }
                }

                VStack {
                    Spacer()
                    HStack {
                        VStack(alignment: .leading, spacing: theme.spacing.xxSmall) {
                            Text(episode.showName)
                                .textStyle(theme.typography.titleMedium)
                                .foregroundColor(theme.colors.onPrimary)
                                .lineLimit(1)

                            Text(episode.episodeNumber)
                                .textStyle(theme.typography.bodyMedium)
                                .foregroundColor(theme.colors.onPrimary.opacity(0.8))
                                .lineLimit(1)
                        }
                        Spacer()
                    }
                    .padding(theme.spacing.small)
                }
            }
        }
        .buttonStyle(PlainButtonStyle())
        .frame(width: DimensionConstants.imageWidth, height: DimensionConstants.imageHeight)
        .cornerRadius(theme.shapes.medium)
        .shadow(color: Color.black.opacity(0.2), radius: 4, x: 0, y: 2)
        .contextMenu {
            Button {
                onMarkWatched()
            } label: {
                Label(markWatchedLabel, systemImage: "checkmark.circle")
            }

            Button {
                onUnfollowShow()
            } label: {
                Label(unfollowShowLabel, systemImage: "minus.circle")
            }

            Button {
                onOpenSeason()
            } label: {
                Label(openSeasonLabel, systemImage: "list.bullet")
            }
        }
    }
}

private enum DimensionConstants {
    static let imageWidth: CGFloat = 300
    static let imageHeight: CGFloat = 160
}

#Preview {
    VStack {
        NextEpisodeCard(
            episode: SwiftNextEpisode(
                showTraktId: 1,
                showName: "The Walking Dead: Daryl Dixon",
                showPoster: "/poster.jpg",
                episodeId: 123,
                episodeTitle: "L'âme Perdue",
                episodeNumber: "S02E01",
                runtime: "45 min",
                stillImage: "/still.jpg",
                overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                badge: .new
            ),
            markWatchedLabel: "Mark as Watched",
            unfollowShowLabel: "Unfollow Show",
            openSeasonLabel: "Open Season",
            onEpisodeClick: { _, _ in }
        )
        .padding()
    }
}
