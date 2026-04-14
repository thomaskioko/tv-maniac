import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct WatchlistTab: View {
    private let presenter: WatchlistPresenter
    @StateValue private var uiState: WatchlistState

    @State private var watchNextEpisodesSwift: [SwiftNextEpisode] = []
    @State private var staleEpisodesSwift: [SwiftNextEpisode] = []

    init(presenter: WatchlistPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    var body: some View {
        WatchlistScreen(
            title: String(\.label_tab_watchlist),
            searchPlaceholder: String(\.label_search_placeholder),
            emptyText: String(\.generic_empty_content),
            upToDateText: String(\.label_up_to_date),
            listStyleLabel: String(\.label_watchlist_list_style),
            searchLabel: String(\.label_tab_search),
            sortLabel: String(\.label_watchlist_sort_list),
            upNextSectionTitle: String(\.label_discover_up_next),
            staleSectionTitle: String(\.title_not_watched_for_while),
            premiereLabel: String(\.badge_premiere),
            newLabel: String(\.badge_new),
            isLoading: uiState.showLoading,
            isGridMode: uiState.isGridMode,
            isSearchActive: uiState.isSearchActive,
            query: uiState.query,
            watchNextGridItems: Array(uiState.watchNextItems).map {
                WatchlistGridItem(
                    traktId: $0.traktId,
                    title: $0.title,
                    posterImageUrl: $0.posterImageUrl,
                    watchProgress: $0.watchProgress
                )
            },
            staleGridItems: Array(uiState.staleItems).map {
                WatchlistGridItem(
                    traktId: $0.traktId,
                    title: $0.title,
                    posterImageUrl: $0.posterImageUrl,
                    watchProgress: $0.watchProgress
                )
            },
            watchNextEpisodes: watchNextEpisodesSwift,
            staleEpisodes: staleEpisodesSwift,
            onQueryChanged: { presenter.dispatch(action: WatchlistQueryChanged(query: $0)) },
            onQueryCleared: { presenter.dispatch(action: ClearWatchlistQuery()) },
            onToggleListStyle: {
                presenter.dispatch(action: ChangeListStyleClicked_(isGridMode: uiState.isGridMode))
            },
            onToggleSearch: { presenter.dispatch(action: ToggleSearchActive_()) },
            onShowClicked: { id in presenter.dispatch(action: WatchlistShowClicked(traktId: id)) },
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
            }
        )
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
