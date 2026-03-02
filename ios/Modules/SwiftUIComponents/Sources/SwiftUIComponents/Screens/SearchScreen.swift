import SwiftUI

public enum SearchScreenState {
    case loading
    case empty
    case searchResults(results: [SwiftSearchShow], isUpdating: Bool)
    case browsingGenres(genres: [SwiftGenreRow], isRefreshing: Bool)
    case error(message: String)
}

public struct SearchScreen: View {
    @Theme private var theme

    private let state: SearchScreenState
    @Binding private var query: String
    private let searchPrompt: String
    private let emptyResultsMessage: String
    private let retryButtonText: String
    private let selectedCategory: String
    private let categories: [String]
    private let categoryTitle: String
    private let onShowClicked: (Int64) -> Void
    private let onRetry: () -> Void
    private let onCategoryChanged: (String) -> Void

    @State private var showFilterSheet = false

    public init(
        state: SearchScreenState,
        query: Binding<String>,
        searchPrompt: String,
        emptyResultsMessage: String,
        retryButtonText: String,
        selectedCategory: String = "",
        categories: [String] = [],
        categoryTitle: String = "Category",
        onShowClicked: @escaping (Int64) -> Void,
        onRetry: @escaping () -> Void,
        onCategoryChanged: @escaping (String) -> Void = { _ in }
    ) {
        self.state = state
        _query = query
        self.searchPrompt = searchPrompt
        self.emptyResultsMessage = emptyResultsMessage
        self.retryButtonText = retryButtonText
        self.selectedCategory = selectedCategory
        self.categories = categories
        self.categoryTitle = categoryTitle
        self.onShowClicked = onShowClicked
        self.onRetry = onRetry
        self.onCategoryChanged = onCategoryChanged
    }

    private var isBrowsingGenres: Bool {
        if case .browsingGenres = state { return true }
        return false
    }

    public var body: some View {
        ZStack {
            theme.colors.background
                .ignoresSafeArea()

            ScrollView(showsIndicators: false) {
                contentView
                    .padding(.top, theme.spacing.medium)
            }
        }
        .searchable(
            text: $query,
            placement: .navigationBarDrawer(displayMode: .always),
            prompt: searchPrompt
        )
        .disableAutocorrection(true)
        .textInputAutocapitalization(.never)
        .scrollContentBackground(.hidden)
        .toolbarBackground(.visible, for: .navigationBar)
        .toolbarBackground(theme.colors.surface, for: .navigationBar)
        .toolbar {
            if isBrowsingGenres {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        showFilterSheet = true
                    } label: {
                        Image(systemName: "line.3.horizontal.decrease")
                            .foregroundColor(theme.colors.onSurface)
                    }
                }
            }
        }
        .sheet(isPresented: $showFilterSheet) {
            filterSheetContent
                .presentationDetents([.medium])
        }
    }

    private var filterSheetContent: some View {
        VStack(spacing: theme.spacing.medium) {
            FilterChipSection(
                title: categoryTitle,
                items: categories,
                selectedItems: Set([selectedCategory]),
                labelProvider: { $0 },
                onItemToggle: { category in
                    onCategoryChanged(category)
                    showFilterSheet = false
                }
            )
            .padding(.horizontal, theme.spacing.medium)
            .padding(.top, theme.spacing.medium)

            Spacer()
        }
    }

    @ViewBuilder
    private var contentView: some View {
        switch state {
        case .loading:
            loadingView
                .transition(.opacity)
        case .empty:
            emptyStateView
                .transition(.opacity)
        case let .searchResults(results, isUpdating):
            searchResultsView(results: results, isUpdating: isUpdating)
                .transition(.opacity)
        case let .browsingGenres(genres, isRefreshing):
            genreRowsSection(genreRows: genres, isUpdating: isRefreshing)
        case let .error(message):
            errorView(message: message)
                .transition(.opacity)
        }
    }

    private func genreRowsSection(genreRows: [SwiftGenreRow], isUpdating: Bool) -> some View {
        VStack(spacing: 0) {
            if isUpdating {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle())
                    .scaleEffect(1.5)
                    .tint(.accentColor)
                    .padding(.horizontal)
                    .padding(.bottom, theme.spacing.xSmall)
            }

            ForEach(genreRows, id: \.id) { genreRow in
                HorizontalShowContentView(
                    title: genreRow.name,
                    subtitle: genreRow.subtitle,
                    chevronStyle: .chevronOnly,
                    items: genreRow.shows,
                    onClick: { id in
                        onShowClicked(id)
                    }
                )
            }
        }
    }

    private func searchResultsView(results: [SwiftSearchShow], isUpdating: Bool) -> some View {
        VStack {
            if isUpdating {
                LoadingIndicatorView()
            }

            SearchResultListView(
                items: results,
                onClick: { id in
                    onShowClicked(id)
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
        EmptyStateView(
            systemName: "exclamationmark.magnifyingglass",
            title: emptyResultsMessage
        )
    }

    private func errorView(message: String) -> some View {
        EmptyStateView(
            systemName: "exclamationmark.arrow.triangle.2.circlepath",
            title: message,
            buttonText: retryButtonText,
            action: onRetry
        )
        .frame(height: 200)
    }
}

#Preview("Loading") {
    ThemedPreview {
        NavigationStack {
            SearchScreen(
                state: .loading,
                query: .constant(""),
                searchPrompt: "Search for shows",
                emptyResultsMessage: "No results found",
                retryButtonText: "Retry",
                onShowClicked: { _ in },
                onRetry: {}
            )
            .navigationTitle("Search")
            .navigationBarTitleDisplayMode(.large)
        }
    }
    .preferredColorScheme(.dark)
}

#Preview("Browsing Genres") {
    ThemedPreview {
        NavigationStack {
            SearchScreen(
                state: .browsingGenres(
                    genres: [
                        SwiftGenreRow(
                            id: "action",
                            name: "Action",
                            subtitle: "High-octane thrills",
                            shows: [
                                .init(traktId: 1, title: "Arcane", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                                .init(traktId: 2, title: "The Penguin", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                            ]
                        ),
                        SwiftGenreRow(
                            id: "drama",
                            name: "Drama",
                            subtitle: "Compelling stories",
                            shows: [
                                .init(traktId: 3, title: "Kaos", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                                .init(traktId: 4, title: "One Piece", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                            ]
                        ),
                    ],
                    isRefreshing: false
                ),
                query: .constant(""),
                searchPrompt: "Search for shows",
                emptyResultsMessage: "No results found",
                retryButtonText: "Retry",
                onShowClicked: { _ in },
                onRetry: {}
            )
            .navigationTitle("Search")
            .navigationBarTitleDisplayMode(.large)
        }
    }
    .preferredColorScheme(.dark)
}

#Preview("Search Results") {
    ThemedPreview {
        NavigationStack {
            SearchScreen(
                state: .searchResults(
                    results: [
                        .init(
                            tmdbId: 44234, traktId: 44234, title: "The Penguin",
                            overview: "Follow Oswald Oz Cobb's quest for control.",
                            status: "Ended",
                            imageUrl: "https://image.tmdb.org/t/p/w780/VSRmtRlYgd0pBISf7d34TAwWgB.jpg",
                            year: "2024", voteAverage: 8.5
                        ),
                        .init(
                            tmdbId: 1234, traktId: 1234, title: "Kaos",
                            overview: "A renegade fighter battles a powerful robot.",
                            status: "Ended",
                            imageUrl: "https://image.tmdb.org/t/p/w780/9Piw6Zju39bn3enIDLZzPfjMTBR.jpg",
                            year: "2024", voteAverage: 7.2
                        ),
                    ],
                    isUpdating: false
                ),
                query: .constant("penguin"),
                searchPrompt: "Search for shows",
                emptyResultsMessage: "No results found",
                retryButtonText: "Retry",
                onShowClicked: { _ in },
                onRetry: {}
            )
            .navigationTitle("Search")
            .navigationBarTitleDisplayMode(.large)
        }
    }
    .preferredColorScheme(.dark)
}

#Preview("Empty Results") {
    ThemedPreview {
        NavigationStack {
            SearchScreen(
                state: .empty,
                query: .constant("xyzabc"),
                searchPrompt: "Search for shows",
                emptyResultsMessage: "No results found",
                retryButtonText: "Retry",
                onShowClicked: { _ in },
                onRetry: {}
            )
            .navigationTitle("Search")
            .navigationBarTitleDisplayMode(.large)
        }
    }
    .preferredColorScheme(.dark)
}

#Preview("Error") {
    ThemedPreview {
        NavigationStack {
            SearchScreen(
                state: .error(message: "Something went wrong. Please try again."),
                query: .constant(""),
                searchPrompt: "Search for shows",
                emptyResultsMessage: "No results found",
                retryButtonText: "Retry",
                onShowClicked: { _ in },
                onRetry: {}
            )
            .navigationTitle("Search")
            .navigationBarTitleDisplayMode(.large)
        }
    }
    .preferredColorScheme(.dark)
}
