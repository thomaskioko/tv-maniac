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
            title: String(\.label_library_title),
            searchPlaceholder: String(\.label_search_placeholder),
            emptyText: String(\.generic_empty_content),
            isLoading: uiState.showLoading,
            isRefreshing: uiState.isRefreshing,
            isEmpty: uiState.isEmpty,
            isGridMode: uiState.isGridMode,
            isSearchActive: uiState.isSearchActive,
            query: uiState.query,
            gridItems: Array(uiState.items).map {
                LibraryGridItem(traktId: $0.traktId, title: $0.title, posterImageUrl: $0.posterImageUrl)
            },
            listItems: Array(uiState.items).map { $0.toSwift() },
            emptySearchResultFormat: { query in String(\.label_watchlist_empty_result, parameter: query) },
            onQueryChanged: { presenter.dispatch(action_____: LibraryQueryChanged(query: $0)) },
            onQueryCleared: { presenter.dispatch(action_____: ClearLibraryQuery()) },
            onToggleListStyle: {
                presenter.dispatch(action_____: ChangeListStyleClicked(isGridMode: uiState.isGridMode))
            },
            onToggleSearch: { presenter.dispatch(action_____: ToggleSearchActive()) },
            onSortClicked: { showSortOptions = true },
            onShowClicked: { id in presenter.dispatch(action_____: LibraryShowClicked(traktId: id)) }
        )
        .sheet(isPresented: $showSortOptions) {
            SortOptionsSheet(
                state: uiState,
                onSortOptionSelected: { sortOption in
                    presenter.dispatch(action_____: ChangeSortOption(sortOption: sortOption))
                },
                onGenreToggle: { genre in
                    presenter.dispatch(action_____: ToggleGenreFilter(genre: genre))
                },
                onStatusToggle: { status in
                    presenter.dispatch(action_____: ToggleStatusFilter(status: status))
                },
                onClearFilters: {
                    presenter.dispatch(action_____: ClearFilters())
                },
                onApplyFilters: {
                    showSortOptions = false
                }
            )
            .presentationDetents([.large])
        }
    }
}
