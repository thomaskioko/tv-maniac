import SwiftUI
import SwiftUIComponents
import TvManiac
import TvManiacKit

struct SearchTab: View {
    @Theme private var theme

    private let presenter: SearchShowsPresenter
    @StateObject @KotlinStateFlow private var uiState: SearchShowState
    @FocusState private var isSearchFocused: Bool
    @State private var showGlass: Double = 0

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
        ZStack {
            theme.colors.background
                .ignoresSafeArea()

            ScrollView(showsIndicators: false) {
                contentView
                    .padding(.top, 16)
            }
        }
        .navigationTitle(String(\.label_search_title))
        .navigationBarTitleDisplayMode(.large)
        .searchable(
            text: searchQueryBinding,
            placement: .navigationBarDrawer(displayMode: .always),
            prompt: String(\.label_search_placeholder)
        )
        .disableAutocorrection(true)
        .textInputAutocapitalization(.never)
        .scrollContentBackground(.hidden)
        .toolbarBackground(.visible, for: .navigationBar)
        .toolbarBackground(theme.colors.surface, for: .navigationBar)
    }

    @ViewBuilder
    private var contentView: some View {
        switch onEnum(of: uiState.uiState) {
        case .initialLoading, .searchLoading:
            loadingView
                .transition(.opacity)
        case .searchEmpty:
            emptyStateView
                .transition(.opacity)
        case let .searchResults(state):
            searchResultsView(results: state.results, isUpdating: state.isUpdating)
                .transition(.opacity)
        case let .browsingGenres(state):
            genreSection(genres: Array(state.genres), isUpdating: state.isRefreshing)
        case let .error(state):
            errorView(message: state.message)
                .transition(.opacity)
        }
    }

    private func genreSection(genres: [ShowGenre], isUpdating: Bool) -> some View {
        Section {
            let items = genres.map {
                $0.toSwift()
            }
            let columns = [GridItem(.adaptive(minimum: 160), spacing: 8)]

            ZStack {
                LazyVGrid(columns: columns, spacing: 8) {
                    ForEach(items, id: \.id) { item in
                        PosterCardView(
                            title: item.name,
                            posterUrl: item.imageUrl,
                            posterWidth: 170,
                            posterHeight: 220
                        )
                        .clipped()
                        .onTapGesture {
                            withAnimation(.none) {
                                presenter.dispatch(action: GenreCategoryClicked(id: item.tmdbId))
                            }
                        }
                    }
                }

                if isUpdating {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                        .scaleEffect(1.5)
                        .tint(.accentColor)
                        .padding(.horizontal)
                        .padding(.bottom, 8)
                }
            }
        } header: {
            HStack {
                Text(String(\.label_search_by_genre))
                    .textStyle(theme.typography.titleLarge)
                Spacer()
            }
        }
        .padding(.horizontal)
    }

    private func searchResultsView(results: [ShowItem], isUpdating: Bool) -> some View {
        VStack {
            if isUpdating {
                LoadingIndicatorView()
            }

            SearchResultListView(
                items: results.map {
                    $0.toSwift()
                },
                onClick: { id in
                    presenter.dispatch(action: SearchShowClicked(id: id))
                    isSearchFocused = false
                }
            )
        }
    }

    private var loadingView: some View {
        CenteredFullScreenView {
            LoadingIndicatorView()
        }
    }

    private var emptyStateView: some View {
        FullScreenView(
            systemName: "exclamationmark.magnifyingglass",
            message: String(\.label_search_empty_results)
        )
    }

    private func errorView(message: String) -> some View {
        FullScreenView(
            systemName: "exclamationmark.arrow.triangle.2.circlepath",
            message: message,
            buttonText: String(\.button_error_retry),
            action: { presenter.dispatch(action: ReloadShowContent()) }
        )
        .frame(maxWidth: .infinity)
        .frame(height: 200)
    }
}
