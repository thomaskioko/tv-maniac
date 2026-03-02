import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct SearchTab: View {
    private let presenter: SearchShowsPresenter
    @StateObject @KotlinStateFlow private var uiState: SearchShowState

    init(presenter: SearchShowsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    private var searchQueryBinding: Binding<String> {
        BindingFactories.searchQuery(
            get: { uiState.query },
            onChanged: { presenter.dispatch(action: QueryChanged(query: $0)) },
            onCleared: { presenter.dispatch(action: ClearQuery()) }
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
            onShowClicked: { id in presenter.dispatch(action: SearchShowClicked(id: id)) },
            onRetry: { presenter.dispatch(action: ReloadShowContent()) },
            onBack: { presenter.dispatch(action: BackClicked_()) },
            onCategoryChanged: { label in
                if let item = categoryLabels.first(where: { $0.label == label }) {
                    presenter.dispatch(action: CategoryChanged(category: item.category))
                }
            }
        )
    }

    private func mapState(_ uiState: SearchUiState) -> SearchScreenState {
        switch onEnum(of: uiState) {
        case .initialLoading, .searchLoading:
            .loading
        case .searchEmpty:
            .empty
        case let .searchResults(state):
            .searchResults(
                results: state.results.map { ($0 as ShowItem).toSwift() },
                isUpdating: state.isUpdating
            )
        case let .browsingGenres(state):
            .browsingGenres(
                genres: Array(state.genreRows).map { $0.toSwift() },
                isRefreshing: state.isRefreshing
            )
        case let .error(state):
            .error(message: state.message)
        }
    }
}
