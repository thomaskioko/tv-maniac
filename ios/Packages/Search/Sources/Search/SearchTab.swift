import Components
import DesignSystem
import SwiftUI
import TvManiac
import TvManiacKit

public struct SearchTab: View {
    private let presenter: SearchShowsPresenter
    @StateValue private var uiState: SearchShowState

    public init(presenter: SearchShowsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
    }

    private var searchQueryBinding: Binding<String> {
        BindingFactories.searchQuery(
            get: { uiState.query },
            onChanged: { presenter.dispatch(action: QueryChanged(query: $0)) },
            onCleared: { presenter.dispatch(action: ClearQuery()) }
        )
    }

    public var body: some View {
        let categoryLabels = Array(uiState.categories).map { $0 as CategoryItem }

        SearchScreen(
            state: uiState.toState(categoryLabels: categoryLabels),
            query: searchQueryBinding,
            onShowClicked: { id in presenter.dispatch(action: SearchShowClicked(showId: id)) },
            onRetry: { presenter.dispatch(action: ReloadShowContent()) },
            onBack: { presenter.dispatch(action: BackClicked__()) },
            onCategoryChanged: { label in
                if let item = categoryLabels.first(where: { $0.label == label }) {
                    presenter.dispatch(action: CategoryChanged(category: item.category))
                }
            }
        )
    }
}

private extension SearchShowState {
    func toState(categoryLabels: [CategoryItem]) -> SearchScreen.State {
        SearchScreen.State(
            title: String(\.label_search_title),
            screenState: uiState.toScreenState(),
            searchPlaceholder: String(\.label_search_placeholder),
            emptyResultsMessage: String(\.label_search_empty_results),
            retryButtonText: String(\.button_error_retry),
            selectedCategory: categoryLabels.first { $0.category == selectedCategory }?.label ?? "",
            categories: categoryLabels.map(\.label),
            categoryTitle: categoryTitle
        )
    }
}

private extension SearchUiState {
    func toScreenState() -> SearchScreenState {
        switch self {
        case is SearchUiStateInitialLoading:
            .loading
        case is SearchUiStateSearchLoading:
            .searchLoading
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
