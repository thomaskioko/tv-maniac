import SwiftUI

public struct NextEpisodesSection: View {
    private let title: String
    private let episodes: [SwiftNextEpisode]
    private let chevronStyle: ChevronStyle
    private let onEpisodeClick: (Int64, Int64) -> Void
    private let onSeeAllClick: () -> Void

    public init(
        title: String,
        episodes: [SwiftNextEpisode],
        chevronStyle: ChevronStyle = .none,
        onEpisodeClick: @escaping (Int64, Int64) -> Void,
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
                                onEpisodeClick: onEpisodeClick
                            )
                            .padding([.leading, .trailing], 6)
                            .padding(.leading, episode.episodeId == episodes.first?.episodeId ? 10 : 0)
                            .padding(.trailing, episode.episodeId == episodes.last?.episodeId ? 8 : 0)
                        }
                    }
                }
            }
            .padding(.bottom)
        }
    }
}

#Preview {
    VStack {
        NextEpisodesSection(
            title: "Up Next",
            episodes: [
                SwiftNextEpisode(
                    showId: 123,
                    showName: "The Walking Dead: Daryl Dixon",
                    showPoster: "/some-poster.jpg",
                    episodeId: 456,
                    episodeTitle: "L'âme Perdue",
                    episodeNumber: "S02E01",
                    runtime: "45 min",
                    stillImage: "https://image.tmdb.org/t/p/w780/ydlY3iPfeOAvu8gVqrxPoMvzNCn.jpg",
                    overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                    isNew: true
                ),
                SwiftNextEpisode(
                    showId: 124,
                    showName: "Wednesday",
                    showPoster: "/another-poster.jpg",
                    episodeId: 789,
                    episodeTitle: "Wednesday's Child Is Full of Woe",
                    episodeNumber: "S02E02",
                    runtime: "50 min",
                    stillImage: "https://image.tmdb.org/t/p/w780/dC0oTEMAPnBzM0RU15qArHoqnH5.jpg",
                    overview: "Wednesday arrives at Nevermore Academy.",
                    isNew: false
                ),
                SwiftNextEpisode(
                    showId: 125,
                    showName: "House of the Dragon",
                    showPoster: "/dragon-poster.jpg",
                    episodeId: 790,
                    episodeTitle: "The Heirs of the Dragon",
                    episodeNumber: "S03E01",
                    runtime: "66 min",
                    stillImage: "https://image.tmdb.org/t/p/w780/dragon-still.jpg",
                    overview: "King Viserys hosts a tournament.",
                    isNew: true
                ),
            ],
            chevronStyle: .chevronOnly,
            onEpisodeClick: { _, _ in }
        )
    }
}
