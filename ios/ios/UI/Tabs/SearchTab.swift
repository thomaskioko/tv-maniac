import SwiftUI
import SwiftUIComponents
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

    // MARK: - Bindings

    private var searchQueryBinding: Binding<String> {
        Binding(
            get: { uiState.query ?? "" },
            set: { newValue in
                let trimmedValue = newValue.trimmingCharacters(in: .whitespaces)
                if !trimmedValue.isEmpty {
                    presenter.dispatch(action: QueryChanged(query: newValue))
                } else {
                    presenter.dispatch(action: ClearQuery())
                }
            }
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
        switch onEnum(of: uiState) {
        case .initialSearchState:
            loadingView
                .transition(.opacity)
        case let .searchResultAvailable(state):
            searchResultsView(state: state)
                .transition(.opacity)
        case let .showContentAvailable(state):
            genreSection(state: state)
        case let .emptySearchResult(state):
            if state.errorMessage != nil {
                errorView(state: state)
                    .transition(.opacity)
            } else if !state.isUpdating {
                emptyStateView
                    .transition(.opacity)
            }
        }
    }

    @ViewBuilder
    private func genreSection(state: ShowContentAvailable) -> some View {
        Section {
            let items = state.genres.map {
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

                if state.isUpdating {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                        .scaleEffect(1.5)
                        .tint(.gray)
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

    @ViewBuilder
    private func searchResultsView(state: SearchResultAvailable) -> some View {
        VStack {
            if state.isUpdating {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle())
                    .scaleEffect(1.5)
                    .tint(.gray)
                    .padding(.horizontal)
                    .padding(.bottom, 8)
            }

            SearchResultListView(
                items: state.results.map {
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
            LoadingIndicatorView(animate: true)
        }
    }

    private var emptyStateView: some View {
        FullScreenView(
            systemName: "exclamationmark.magnifyingglass",
            message: String(\.label_search_empty_results)
        )
    }

    private func errorView(state: EmptySearchResult) -> some View {
        FullScreenView(
            systemName: "exclamationmark.arrow.triangle.2.circlepath",
            message: state.errorMessage ?? String(\.label_search_empty_results),
            buttonText: String(\.button_error_retry),
            action: { presenter.dispatch(action: ReloadShowContent()) }
        )
        .frame(maxWidth: .infinity)
        .frame(height: 200)
    }
}
