import SwiftUI

public struct NextEpisodesSection: View {
    @Theme private var theme

    private let title: String
    private let episodes: [SwiftNextEpisode]
    private let chevronStyle: ChevronStyle
    private let markWatchedLabel: String
    private let unfollowShowLabel: String
    private let openSeasonLabel: String
    private let onEpisodeClick: (Int64, Int64) -> Void
    private let onSeeAllClick: () -> Void
    private let onMarkWatched: (SwiftNextEpisode) -> Void
    private let onUnfollowShow: (SwiftNextEpisode) -> Void
    private let onOpenSeason: (SwiftNextEpisode) -> Void

    public init(
        title: String,
        episodes: [SwiftNextEpisode],
        chevronStyle: ChevronStyle = .none,
        markWatchedLabel: String,
        unfollowShowLabel: String,
        openSeasonLabel: String,
        onEpisodeClick: @escaping (Int64, Int64) -> Void,
        onSeeAllClick: @escaping () -> Void = {},
        onMarkWatched: @escaping (SwiftNextEpisode) -> Void = { _ in },
        onUnfollowShow: @escaping (SwiftNextEpisode) -> Void = { _ in },
        onOpenSeason: @escaping (SwiftNextEpisode) -> Void = { _ in }
    ) {
        self.title = title
        self.episodes = episodes
        self.chevronStyle = chevronStyle
        self.markWatchedLabel = markWatchedLabel
        self.unfollowShowLabel = unfollowShowLabel
        self.openSeasonLabel = openSeasonLabel
        self.onEpisodeClick = onEpisodeClick
        self.onSeeAllClick = onSeeAllClick
        self.onMarkWatched = onMarkWatched
        self.onUnfollowShow = onUnfollowShow
        self.onOpenSeason = onOpenSeason
    }

    public var body: some View {
        if !episodes.isEmpty {
            VStack {
                ChevronTitle(
                    title: title,
                    chevronStyle: chevronStyle,
                    action: onSeeAllClick
                )

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack {
                        ForEach(episodes, id: \.episodeId) { episode in
                            NextEpisodeCard(
                                episode: episode,
                                markWatchedLabel: markWatchedLabel,
                                unfollowShowLabel: unfollowShowLabel,
                                openSeasonLabel: openSeasonLabel,
                                onEpisodeClick: onEpisodeClick,
                                onMarkWatched: { onMarkWatched(episode) },
                                onUnfollowShow: { onUnfollowShow(episode) },
                                onOpenSeason: { onOpenSeason(episode) }
                            )
                            .padding([.leading, .trailing], theme.spacing.xxSmall + 2)
                            .padding(.leading, episode.episodeId == episodes.first?.episodeId ? theme.spacing.small - 2 : 0)
                            .padding(.trailing, episode.episodeId == episodes.last?.episodeId ? theme.spacing.xSmall : 0)
                        }
                    }
                }
            }
            .padding(.bottom, theme.spacing.medium)
        }
    }
}

#Preview {
    VStack {
        NextEpisodesSection(
            title: "Up Next",
            episodes: [
                SwiftNextEpisode(
                    showTraktId: 123,
                    showName: "The Walking Dead: Daryl Dixon",
                    imageUrl: "https://image.tmdb.org/t/p/w780/ydlY3iPfeOAvu8gVqrxPoMvzNCn.jpg",
                    episodeId: 456,
                    episodeTitle: "L'âme Perdue",
                    episodeNumber: "S02E01",
                    runtime: "45 min",
                    overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                    badge: .premiere
                ),
                SwiftNextEpisode(
                    showTraktId: 124,
                    showName: "Wednesday",
                    imageUrl: "https://image.tmdb.org/t/p/w780/dC0oTEMAPnBzM0RU15qArHoqnH5.jpg",
                    episodeId: 789,
                    episodeTitle: "Wednesday's Child Is Full of Woe",
                    episodeNumber: "S02E02",
                    runtime: "50 min",
                    overview: "Wednesday arrives at Nevermore Academy.",
                    badge: .new
                ),
                SwiftNextEpisode(
                    showTraktId: 125,
                    showName: "House of the Dragon",
                    imageUrl: "https://image.tmdb.org/t/p/w780/dragon-still.jpg",
                    episodeId: 790,
                    episodeTitle: "The Heirs of the Dragon",
                    episodeNumber: "S03E01",
                    runtime: "66 min",
                    overview: "King Viserys hosts a tournament.",
                    badge: .new
                ),
            ],
            chevronStyle: .chevronOnly,
            markWatchedLabel: "Mark as Watched",
            unfollowShowLabel: "Unfollow Show",
            openSeasonLabel: "Open Season",
            onEpisodeClick: { _, _ in }
        )
    }
}
