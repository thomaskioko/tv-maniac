import DesignSystem
import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct MyShowsTab: View {
    private let presenter: ContinueWatchingPresenter
    @StateValue private var uiState: ContinueWatchingState

    @State private var watchNextEpisodesSwift: [SwiftNextEpisode] = []
    @State private var staleEpisodesSwift: [SwiftNextEpisode] = []
    @State private var toast: Toast?
    @State private var showSortOptions = false

    init(presenter: MyShowsPresenter) {
        self.presenter = presenter.continueWatchingPresenter
        _uiState = .init(presenter.continueWatchingPresenter.stateValue)
    }

    var body: some View {
        MyShowsScreen(
            state: uiState.toState(
                watchNextEpisodes: watchNextEpisodesSwift,
                staleEpisodes: staleEpisodesSwift
            ),
            onQueryChanged: { presenter.dispatch(action: ContinueWatchingQueryChanged(query: $0)) },
            onQueryCleared: { presenter.dispatch(action: ClearContinueWatchingQuery()) },
            onToggleListStyle: {
                presenter.dispatch(action: ChangeContinueWatchingListStyle(isGridMode: uiState.isGridMode))
            },
            onToggleSearch: { presenter.dispatch(action: ToggleContinueWatchingSearch()) },
            onSortClicked: { showSortOptions = true },
            onShowClicked: { id in presenter.dispatch(action: ContinueWatchingShowClicked(traktId: id)) },
            onEpisodeClicked: { showTraktId, episodeId in
                presenter.dispatch(action: UpNextEpisodeClicked(showTraktId: showTraktId, episodeId: episodeId))
            },
            onShowTitleClicked: { showTraktId in
                presenter.dispatch(action: ShowTitleClicked(showTraktId: showTraktId))
            },
            onMarkWatched: { episode in
                presenter.dispatch(action: MarkUpNextEpisodeWatched(
                    showTraktId: episode.showTraktId,
                    episodeId: episode.episodeId,
                    seasonNumber: episode.seasonNumber,
                    episodeNumber: episode.episodeNumberValue
                ))
            },
            onRefresh: { presenter.dispatch(action: RefreshContinueWatching(forceRefresh: true)) }
        )
        .sheet(isPresented: $showSortOptions) {
            MyShowsSortOptionsSheet(
                selectedSortOption: uiState.sortOption,
                onSortOptionSelected: { sortOption in
                    presenter.dispatch(action: ChangeContinueWatchingSortOption(sortOption: sortOption))
                }
            )
            .presentationDetents([.medium, .large])
        }
        .toastView(toast: $toast)
        .onChange(of: uiState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(type: .error, title: String(\.label_error), message: message.message)
                presenter.dispatch(action: ContinueWatchingMessageShown(id: message.id))
            }
        }
        .onChange(of: uiState.watchNextEpisodes) { _, newValue in
            watchNextEpisodesSwift = newValue.map { $0.toSwift() }
        }
        .onChange(of: uiState.staleEpisodes) { _, newValue in
            staleEpisodesSwift = newValue.map { $0.toSwift() }
        }
        .onAppear {
            watchNextEpisodesSwift = uiState.watchNextEpisodes.map { $0.toSwift() }
            staleEpisodesSwift = uiState.staleEpisodes.map { $0.toSwift() }
        }
        .onDisappear {
            watchNextEpisodesSwift.removeAll()
            staleEpisodesSwift.removeAll()
        }
    }
}

private extension ContinueWatchingState {
    func toState(
        watchNextEpisodes: [SwiftNextEpisode],
        staleEpisodes: [SwiftNextEpisode]
    ) -> MyShowsScreen.State {
        MyShowsScreen.State(
            title: String(\.label_tab_my_shows),
            searchPlaceholder: String(\.label_search_placeholder),
            emptyText: emptyStateText,
            upToDateText: String(\.label_up_to_date),
            listStyleLabel: String(\.label_watchlist_list_style),
            searchLabel: String(\.label_tab_search),
            sortLabel: String(\.label_watchlist_sort_list),
            upNextSectionTitle: String(\.label_discover_up_next),
            staleSectionTitle: String(\.title_not_watched_for_while),
            premiereLabel: String(\.badge_premiere),
            newLabel: String(\.badge_new),
            isLoading: showLoading,
            isGridMode: isGridMode,
            isSearchActive: isSearchActive,
            query: query,
            watchNextGridItems: Array(watchNextItems).map {
                MyShowsGridItem(
                    traktId: $0.traktId,
                    title: $0.title,
                    posterImageUrl: $0.posterImageUrl,
                    watchProgress: $0.watchProgress
                )
            },
            staleGridItems: Array(staleItems).map {
                MyShowsGridItem(
                    traktId: $0.traktId,
                    title: $0.title ?? "",
                    posterImageUrl: $0.posterImageUrl,
                    watchProgress: $0.watchProgress
                )
            },
            watchNextEpisodes: watchNextEpisodes,
            staleEpisodes: staleEpisodes,
            updatingEpisodeIds: Set(updatingEpisodeIds.map(\.int64Value))
        )
    }
}
