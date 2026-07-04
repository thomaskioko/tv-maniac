import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit
import UpNext

struct DiscoverUpNextSection: View {
    private let presenter: DiscoverUpNextPresenter
    @StateValue private var state: DiscoverUpNextState

    init(presenter: DiscoverUpNextPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    var body: some View {
        DiscoverUpNextContent(
            title: String(\.label_discover_up_next),
            episodes: state.nextEpisodesSwift,
            onEpisodeClicked: { episode in
                presenter.dispatch(action: DiscoverEpisodeLongPressed(
                    showId: episode.showId,
                    episodeId: episode.episodeId
                ))
            }
        )
    }
}

struct DiscoverUpNextContent: View {
    let title: String
    let episodes: [SwiftNextEpisode]
    let onEpisodeClicked: (SwiftNextEpisode) -> Void

    var body: some View {
        NextEpisodesSection(
            title: title,
            episodes: episodes,
            chevronStyle: .chevronOnly,
            onEpisodeClick: onEpisodeClicked
        )
    }
}

#Preview("Up Next - Empty") {
    DiscoverUpNextContent(
        title: "Up Next",
        episodes: [],
        onEpisodeClicked: { _ in }
    )
    .appPreview()
}

#Preview("Up Next - Content") {
    DiscoverUpNextContent(
        title: "Up Next",
        episodes: [
            SwiftNextEpisode(
                showId: 1,
                showName: "Breaking Bad",
                imageUrl: nil,
                episodeId: 101,
                episodeTitle: "Pilot",
                episodeNumber: "S01E01",
                runtime: "58 min",
                overview: "A high school chemistry teacher turns to manufacturing methamphetamine.",
                badge: .premiere,
                remainingEpisodes: 7,
                watchedCount: 1,
                totalCount: 8
            ),
            SwiftNextEpisode(
                showId: 2,
                showName: "Game of Thrones",
                imageUrl: nil,
                episodeId: 201,
                episodeTitle: "Winter Is Coming",
                episodeNumber: "S01E01",
                runtime: "62 min",
                overview: "Eddard Stark is torn between his family and an old friend.",
                badge: .new,
                remainingEpisodes: 9,
                watchedCount: 0,
                totalCount: 10
            ),
        ],
        onEpisodeClicked: { _ in }
    )
    .appPreview()
}
