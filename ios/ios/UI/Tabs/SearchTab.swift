import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct SearchTab: View {
    private let presenter: SearchShowsPresenter
    @StateValue private var uiState: SearchShowState

    init(presenter: SearchShowsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    private var searchQueryBinding: Binding<String> {
        BindingFactories.searchQuery(
            get: { uiState.query },
            onChanged: { presenter.dispatch(action___________: QueryChanged(query: $0)) },
            onCleared: { presenter.dispatch(action___________: ClearQuery()) }
        )
    }

    var body: some View {
        let categoryLabels = Array(uiState.categories).map { $0 as CategoryItem }

        SearchScreen(
            title: String(\.label_search_title),
            state: mapState(uiState.uiState),
            query: searchQueryBinding,
            searchPlaceholder: String(\.label_search_placeholder),
            emptyResultsMessage: String(\.label_search_empty_results),
            retryButtonText: String(\.button_error_retry),
            selectedCategory: categoryLabels.first { $0.category == uiState.selectedCategory }?.label ?? "",
            categories: categoryLabels.map(\.label),
            categoryTitle: uiState.categoryTitle,
            onShowClicked: { id in presenter.dispatch(action___________: SearchShowClicked(id: id)) },
            onRetry: { presenter.dispatch(action___________: ReloadShowContent()) },
            onBack: { presenter.dispatch(action___________: BackClicked_()) },
            onCategoryChanged: { label in
                if let item = categoryLabels.first(where: { $0.label == label }) {
                    presenter.dispatch(action___________: CategoryChanged(category: item.category))
                }
            }
        )
    }

    private func mapState(_ uiState: SearchUiState) -> SearchScreenState {
        switch uiState {
        case is SearchUiStateInitialLoading, is SearchUiStateSearchLoading:
            .loading
        case is SearchUiStateSearchEmpty:
            .empty
        case let state as SearchUiStateSearchResults:
            .searchResults(
                results: state.results.map { ($0 as ShowItem).toSwift() },
                isUpdating: state.isUpdating
            )
        case let state as SearchUiStateBrowsingGenres:
            .browsingGenres(
                genres: Array(state.genreRows).map { $0.toSwift() },
                isRefreshing: state.isRefreshing
            )
        case let state as SearchUiStateError:
            .error(message: state.message)
        default:
            .loading
        }
    }
}
