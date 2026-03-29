import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct ShowDetailsView: View {
    private let presenter: ShowDetailsPresenter
    @StateObject @KotlinStateFlow private var uiState: ShowDetailsContent
    @State private var showCustomList = false
    @State private var showLoginPrompt = false
    @State private var toast: Toast?

    init(presenter: ShowDetailsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    var body: some View {
        ShowDetailsScreen(
            title: uiState.showDetails.title,
            overview: uiState.showDetails.overview,
            backdropImageUrl: uiState.showDetails.backdropImageUrl,
            posterImageUrl: uiState.showDetails.posterImageUrl,
            status: uiState.showDetails.status ?? "",
            year: uiState.showDetails.year,
            language: uiState.showDetails.language ?? "",
            rating: uiState.showDetails.rating,
            isInLibrary: uiState.showDetails.isInLibrary,
            isRefreshing: uiState.isRefreshing,
            openTrailersInYoutube: uiState.showDetails.hasWebViewInstalled,
            selectedSeasonIndex: Int(uiState.selectedSeasonIndex),
            watchedEpisodesCount: Int32(uiState.showDetails.watchedEpisodesCount),
            totalEpisodesCount: Int32(uiState.showDetails.totalEpisodesCount),
            genreList: uiState.showDetails.genres.map { $0.toSwift() },
            seasonList: uiState.showDetails.seasonsList.map { $0.toSwift() },
            providerList: uiState.showDetails.providers.map { $0.toSwift() },
            trailerList: uiState.showDetails.trailersList.map { $0.toSwift() },
            castsList: uiState.showDetails.castsList.map { $0.toSwift() },
            similarShows: uiState.showDetails.similarShows.map { $0.toSwift() },
            continueTrackingEpisodes: uiState.continueTrackingEpisodes.map { $0.toSwift() },
            continueTrackingScrollIndex: Int(uiState.continueTrackingScrollIndex),
            continueTrackingTitle: String(\.title_continue_tracking),
            dayLabelFormat: { count in String(\.day_label, quantity: count) },
            tbdLabel: String(\.label_tbd),
            trackLabel: String(\.following),
            stopTrackingLabel: String(\.unfollow),
            addToListLabel: String(\.btn_add_to_list),
            similarShowsTitle: String(\.title_similar),
            seasonDetailsTitle: String(\.title_season_details),
            showSeasonDetailsHeader: uiState.continueTrackingEpisodes.isEmpty,
            seasonCountFormat: { count in String(\.season_count, quantity: count) },
            episodesWatchedFormat: { watched, total in
                String(\.episodes_watched, quantity: Int(total), Int(watched), Int(total))
            },
            episodesLeftFormat: { count in String(\.episodes_left, quantity: Int(count), Int(count)) },
            upToDateLabel: String(\.label_up_to_date),
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
                title: uiState.showDetails.title,
                posterUrl: uiState.showDetails.posterImageUrl,
                traktLists: uiState.traktLists.map { $0 as! TraktListModel },
                showCreateField: uiState.showCreateListField,
                isCreatingList: uiState.isCreatingList,
                createListName: uiState.createListName,
                sheetTitle: uiState.sheetTitle,
                createButtonText: uiState.createListButtonText,
                doneButtonText: uiState.createListDoneText,
                emptyListText: uiState.emptyListText,
                createListPlaceholder: uiState.createListPlaceholder,
                listsHeaderText: uiState.listsHeaderText,
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
