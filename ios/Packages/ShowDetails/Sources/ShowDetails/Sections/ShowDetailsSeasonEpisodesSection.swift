import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit
import UpNext

struct ShowDetailsSeasonEpisodesSection: View {
    private let presenter: ShowDetailsSeasonsEpisodesPresenter
    private let showStatus: String?
    @StateValue private var state: ShowDetailsSeasonsEpisodesState

    init(presenter: ShowDetailsSeasonsEpisodesPresenter, showStatus: String?) {
        self.presenter = presenter
        self.showStatus = showStatus
        _state = .init(presenter.stateValue)
    }

    var body: some View {
        let showHeader = state.continueTrackingEpisodes.count == 0
        ContinueTrackingSection(
            title: String(\.title_continue_tracking),
            episodes: Array(state.continueTrackingEpisodes).map { $0.toSwift() },
            scrollIndex: Int(state.continueTrackingScrollIndex),
            dayLabelFormat: { count in String(\.day_label, quantity: count) },
            tbdLabel: String(\.label_tbd),
            onMarkWatched: { episode in
                if episode.isWatched {
                    presenter.dispatch(action: ShowDetailsMarkEpisodeUnwatched(
                        showId: episode.showId,
                        episodeId: episode.episodeId
                    ))
                } else {
                    presenter.dispatch(action: ShowDetailsMarkEpisodeWatched(
                        showId: episode.showId,
                        episodeId: episode.episodeId,
                        seasonNumber: episode.seasonNumber,
                        episodeNumber: episode.episodeNumber
                    ))
                }
            },
            updatingEpisodeIds: Set(Array(state.updatingEpisodeIds).map(\.int64Value))
        )
        SeasonProgressSection(
            title: String(\.title_season_details),
            showHeader: showHeader,
            status: showStatus,
            watchedEpisodesCount: Int32(state.watchedEpisodesCount),
            totalEpisodesCount: Int32(state.totalEpisodesCount),
            seasonsList: Array(state.seasonsList).map { $0.toSwift() },
            selectedSeasonIndex: Int(state.selectedSeasonIndex),
            seasonCountFormat: { count in String(\.season_count, quantity: Int(count)) },
            episodesWatchedFormat: { watched, total in
                String(\.episodes_watched, quantity: Int(total), Int(watched), Int(total))
            },
            episodesLeftFormat: { count in
                String(\.episodes_left, quantity: Int(count), Int(count))
            },
            upToDateLabel: String(\.label_up_to_date),
            onSeasonClicked: { index, season in
                let params = ShowSeasonDetailsParam(
                    showId: season.tvShowId,
                    seasonId: season.seasonId,
                    seasonNumber: season.seasonNumber,
                    selectedSeasonIndex: Int32(index)
                )
                presenter.dispatch(action: ShowDetailsSeasonClicked(params: params))
            }
        )
    }
}
