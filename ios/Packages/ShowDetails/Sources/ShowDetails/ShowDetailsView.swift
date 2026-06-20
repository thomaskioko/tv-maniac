import Components
import DesignSystem
import SwiftUI
import TvManiacKit

public struct ShowDetailsView: View {
    private let presenter: ShowDetailsPresenter
    @StateValue private var uiState: ShowDetailsContent
    @State private var toast: Toast?

    public init(presenter: ShowDetailsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    public var body: some View {
        ShowDetailsScreen(
            state: uiState.toState(),
            dayLabelFormat: { count in String(\.day_label, quantity: count) },
            seasonCountFormat: { count in String(\.season_count, quantity: count) },
            episodesWatchedFormat: { watched, total in
                String(\.episodes_watched, quantity: Int(total), Int(watched), Int(total))
            },
            episodesLeftFormat: { count in String(\.episodes_left, quantity: Int(count), Int(count)) },
            toast: $toast,
            onBack: { presenter.dispatch(action: DetailBackClicked()) },
            onRefresh: { presenter.dispatch(action: ReloadShowDetails()) },
            onAddToCustomList: { presenter.dispatch(action: OpenShowList()) },
            onAddToLibrary: {
                presenter.dispatch(action: FollowShowClicked(isInLibrary: uiState.showDetails.isInLibrary))
            },
            onSeasonClicked: { index, season in
                let params = ShowSeasonDetailsParam(
                    showId: season.tvShowId,
                    seasonId: season.seasonId,
                    seasonNumber: season.seasonNumber,
                    selectedSeasonIndex: Int32(index)
                )
                presenter.dispatch(action: SeasonClicked(params: params))
            },
            onShowClicked: { id in
                presenter.dispatch(action: DetailShowClicked(showId: id))
            },
            onMarkEpisodeWatched: { episode in
                if episode.isWatched {
                    presenter.dispatch(action: MarkEpisodeUnwatched(
                        showId: episode.showId,
                        episodeId: episode.episodeId
                    ))
                } else {
                    presenter.dispatch(action: MarkEpisodeWatched(
                        showId: episode.showId,
                        episodeId: episode.episodeId,
                        seasonNumber: episode.seasonNumber,
                        episodeNumber: episode.episodeNumber
                    ))
                }
            }
        )
        .onChange(of: uiState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(type: .error, title: "Error", message: message.message)
                presenter.dispatch(action: ShowDetailsMessageShown(id: message.id))
            }
        }
    }
}

private extension ShowDetailsContent {
    func toState() -> ShowDetailsScreen.State {
        ShowDetailsScreen.State(
            title: showDetails.title,
            overview: showDetails.overview,
            backdropImageUrl: showDetails.backdropImageUrl,
            posterImageUrl: showDetails.posterImageUrl,
            status: showDetails.status ?? "",
            year: showDetails.year,
            language: showDetails.language ?? "",
            rating: showDetails.rating,
            isInLibrary: showDetails.isInLibrary,
            isRefreshing: isRefreshing,
            openTrailersInYoutube: showDetails.hasWebViewInstalled,
            selectedSeasonIndex: Int(selectedSeasonIndex),
            watchedEpisodesCount: Int32(showDetails.watchedEpisodesCount),
            totalEpisodesCount: Int32(showDetails.totalEpisodesCount),
            genreList: showDetails.genres.map { $0.toSwift() },
            seasonList: showDetails.seasonsList.map { $0.toSwift() },
            providerList: showDetails.providers.map { $0.toSwift() },
            trailerList: showDetails.trailersList.map { $0.toSwift() },
            castsList: showDetails.castsList.map { $0.toSwift() },
            similarShows: showDetails.similarShows.map { $0.toSwift() },
            continueTrackingEpisodes: continueTrackingEpisodes.map { $0.toSwift() },
            continueTrackingScrollIndex: Int(continueTrackingScrollIndex),
            continueTrackingTitle: String(\.title_continue_tracking),
            tbdLabel: String(\.label_tbd),
            trackLabel: String(\.following),
            stopTrackingLabel: String(\.unfollow),
            addToListLabel: String(\.btn_add_to_list),
            similarShowsTitle: String(\.title_similar),
            seasonDetailsTitle: String(\.title_season_details),
            showSeasonDetailsHeader: continueTrackingEpisodes.isEmpty,
            upToDateLabel: String(\.label_up_to_date),
            canAddToList: canAddToList,
            updatingEpisodeIds: Set(updatingEpisodeIds.map(\.int64Value))
        )
    }
}
