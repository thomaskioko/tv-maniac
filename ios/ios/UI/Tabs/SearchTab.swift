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

    // MARK: - Bindings

    private var searchQueryBinding: Binding<String> {
        Binding(
            get: { uiState.query },
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
        let query = uiState.query
        let isUpdating = uiState.isUpdating
        let searchResults = uiState.searchResults
        let genres = uiState.genres
        let errorMessage = uiState.errorMessage

        if let errorMessage {
            // Error state
            errorView(message: errorMessage)
                .transition(.opacity)
        } else if query.isEmpty, genres.isEmpty, isUpdating {
            // Initial loading state
            loadingView
                .transition(.opacity)
        } else if !query.isEmpty, !searchResults.isEmpty {
            // Search results available
            searchResultsView(results: searchResults, isUpdating: isUpdating)
                .transition(.opacity)
        } else if !query.isEmpty, searchResults.isEmpty, !isUpdating {
            // Empty search results
            emptyStateView
                .transition(.opacity)
        } else if !query.isEmpty, isUpdating {
            // Search in progress
            loadingView
                .transition(.opacity)
        } else if !genres.isEmpty {
            // Genre browsing mode
            genreSection(genres: genres, isUpdating: isUpdating)
        } else {
            // Fallback loading
            loadingView
                .transition(.opacity)
        }
    }

    @ViewBuilder
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
    private func searchResultsView(results: [ShowItem], isUpdating: Bool) -> some View {
        VStack {
            if isUpdating {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle())
                    .scaleEffect(1.5)
                    .tint(.gray)
                    .padding(.horizontal)
                    .padding(.bottom, 8)
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
            LoadingIndicatorView(animate: true)
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
