import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct LibraryTab: View {
    private let presenter: LibraryPresenter
    @StateValue private var uiState: LibraryState
    @State private var showSortOptions = false

    init(presenter: LibraryPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    var body: some View {
        LibraryScreen(
            state: uiState.toState(),
            emptySearchResultFormat: { query in String(\.label_watchlist_empty_result, parameter: query) },
            onQueryChanged: { presenter.dispatch(action: LibraryQueryChanged(query: $0)) },
            onQueryCleared: { presenter.dispatch(action: ClearLibraryQuery()) },
            onToggleListStyle: {
                presenter.dispatch(action: ChangeListStyleClicked(isGridMode: uiState.isGridMode))
            },
            onToggleSearch: { presenter.dispatch(action: ToggleSearchActive()) },
            onSortClicked: { showSortOptions = true },
            onShowClicked: { id in presenter.dispatch(action: LibraryShowClicked(traktId: id)) }
        )
        .sheet(isPresented: $showSortOptions) {
            SortOptionsSheet(
                state: uiState,
                onSortOptionSelected: { sortOption in
                    presenter.dispatch(action: ChangeSortOption(sortOption: sortOption))
                },
                onGenreToggle: { genre in
                    presenter.dispatch(action: ToggleGenreFilter(genre: genre))
                },
                onStatusToggle: { status in
                    presenter.dispatch(action: ToggleStatusFilter(status: status))
                },
                onClearFilters: {
                    presenter.dispatch(action: ClearFilters())
                },
                onApplyFilters: {
                    showSortOptions = false
                }
            )
            .presentationDetents([.large])
        }
    }
}

private extension LibraryState {
    func toState() -> LibraryScreen.State {
        LibraryScreen.State(
            title: String(\.label_library_title),
            searchPlaceholder: String(\.label_search_placeholder),
            emptyText: String(\.generic_empty_content),
            isLoading: showLoading,
            isRefreshing: isRefreshing,
            isEmpty: isEmpty,
            isGridMode: isGridMode,
            isSearchActive: isSearchActive,
            query: query,
            gridItems: Array(items).map {
                LibraryGridItem(traktId: $0.traktId, title: $0.title, posterImageUrl: $0.posterImageUrl)
            },
            listItems: Array(items).map { $0.toSwift() }
        )
    }
}
