import DesignSystem
import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct ShowDetailsView: View {
    private let presenter: ShowDetailsPresenter
    @StateValue private var uiState: ShowDetailsContent
    @State private var showCustomList = false
    @State private var showLoginPrompt = false
    @State private var toast: Toast?

    init(presenter: ShowDetailsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    var body: some View {
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
            onAddToCustomList: { presenter.dispatch(action: ShowShowsListSheet()) },
            onAddToLibrary: {
                presenter.dispatch(action: FollowShowClicked(isInLibrary: uiState.showDetails.isInLibrary))
            },
            onSeasonClicked: { index, season in
                let params = ShowSeasonDetailsParam(
                    showTraktId: season.tvShowId,
                    seasonId: season.seasonId,
                    seasonNumber: season.seasonNumber,
                    selectedSeasonIndex: Int32(index)
                )
                presenter.dispatch(action: SeasonClicked(params: params))
            },
            onShowClicked: { id in
                presenter.dispatch(action: DetailShowClicked(id: id))
            },
            onMarkEpisodeWatched: { episode in
                if episode.isWatched {
                    presenter.dispatch(action: MarkEpisodeUnwatched(
                        showTraktId: episode.showTraktId,
                        episodeId: episode.episodeId
                    ))
                } else {
                    presenter.dispatch(action: MarkEpisodeWatched(
                        showTraktId: episode.showTraktId,
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
        .onChange(of: uiState.showListSheet) { _, newValue in
            showCustomList = newValue
        }
        .onChange(of: showCustomList) { _, newValue in
            if !newValue {
                presenter.dispatch(action: DismissShowsListSheet())
            }
        }
        .onChange(of: uiState.showLoginPrompt) { _, newValue in
            showLoginPrompt = newValue
        }
        .sheet(isPresented: $showCustomList) {
            WatchlistSelector(
                showView: $showCustomList,
                state: uiState.toWatchlistSelectorState(),
                onToggle: { listId, isInList in
                    presenter.dispatch(action: ToggleShowInList(listId: listId, isCurrentlyInList: isInList))
                },
                onShowCreateField: {
                    presenter.dispatch(action: ShowCreateListField())
                },
                onDismissCreateField: {
                    presenter.dispatch(action: DismissCreateListField())
                },
                onCreateListNameChanged: { name in
                    presenter.dispatch(action: UpdateCreateListName(name: name))
                },
                onCreateSubmitted: {
                    presenter.dispatch(action: CreateListSubmitted())
                }
            )
        }
        .alert(uiState.loginRequiredTitle, isPresented: $showLoginPrompt) {
            Button(uiState.loginRequiredConfirmText) {
                presenter.dispatch(action: LoginClicked())
            }
        } message: {
            Text(uiState.loginRequiredMessage)
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
            upToDateLabel: String(\.label_up_to_date)
        )
    }

    func toWatchlistSelectorState() -> WatchlistSelector.State {
        WatchlistSelector.State(
            title: showDetails.title,
            posterUrl: showDetails.posterImageUrl,
            traktLists: traktLists.map { $0.toSwift() },
            showCreateField: showCreateListField,
            isCreatingList: isCreatingList,
            createListName: createListName,
            sheetTitle: sheetTitle,
            createButtonText: createListButtonText,
            doneButtonText: createListDoneText,
            emptyListText: emptyListText,
            createListPlaceholder: createListPlaceholder,
            listsHeaderText: listsHeaderText
        )
    }
}
