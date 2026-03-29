import SwiftUI

public struct NextEpisodesSection: View {
    @Theme private var theme

    private let title: String
    private let episodes: [SwiftNextEpisode]
    private let chevronStyle: ChevronStyle
    private let onEpisodeClick: (SwiftNextEpisode) -> Void
    private let onSeeAllClick: () -> Void

    public init(
        title: String,
        episodes: [SwiftNextEpisode],
        chevronStyle: ChevronStyle = .none,
        onEpisodeClick: @escaping (SwiftNextEpisode) -> Void,
        onSeeAllClick: @escaping () -> Void = {}
    ) {
        self.title = title
        self.episodes = episodes
        self.chevronStyle = chevronStyle
        self.onEpisodeClick = onEpisodeClick
        self.onSeeAllClick = onSeeAllClick
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
                                onEpisodeClick: { onEpisodeClick(episode) }
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
            ],
            chevronStyle: .chevronOnly,
            onEpisodeClick: { _ in }
        )
    }
}
