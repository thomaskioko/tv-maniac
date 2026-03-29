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

    private let title: String
    private let state: SearchScreenState
    @Binding private var query: String
    private let searchPlaceholder: String
    private let emptyResultsMessage: String
    private let retryButtonText: String
    private let selectedCategory: String
    private let categories: [String]
    private let categoryTitle: String
    private let onShowClicked: (Int64) -> Void
    private let onRetry: () -> Void
    private let onBack: () -> Void
    private let onCategoryChanged: (String) -> Void

    @FocusState private var isSearchFocused: Bool
    @State private var showFilterSheet = false

    public init(
        title: String,
        state: SearchScreenState,
        query: Binding<String>,
        searchPlaceholder: String,
        emptyResultsMessage: String,
        retryButtonText: String,
        selectedCategory: String = "",
        categories: [String] = [],
        categoryTitle: String = "Category",
        onShowClicked: @escaping (Int64) -> Void,
        onRetry: @escaping () -> Void,
        onBack: @escaping () -> Void,
        onCategoryChanged: @escaping (String) -> Void = { _ in }
    ) {
        self.title = title
        self.state = state
        _query = query
        self.searchPlaceholder = searchPlaceholder
        self.emptyResultsMessage = emptyResultsMessage
        self.retryButtonText = retryButtonText
        self.selectedCategory = selectedCategory
        self.categories = categories
        self.categoryTitle = categoryTitle
        self.onShowClicked = onShowClicked
        self.onRetry = onRetry
        self.onBack = onBack
        self.onCategoryChanged = onCategoryChanged
    }

    private var isBrowsingGenres: Bool {
        if case .browsingGenres = state { return true }
        return false
    }

    public var body: some View {
        ZStack(alignment: .top) {
            theme.colors.background
                .ignoresSafeArea()

            ScrollView(showsIndicators: false) {
                contentView
                    .padding(.top, theme.spacing.medium)
            }
            .contentMargins(.top, totalHeaderHeight)

            headerOverlay
        }
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .navigationBarColor(backgroundColor: .clear)
        .edgesIgnoringSafeArea(.top)
        .disableAutocorrection(true)
        .textInputAutocapitalization(.never)
        .sheet(isPresented: $showFilterSheet) {
            filterSheetContent
                .presentationDetents([.height(200)])
        }
    }

    @Environment(\.colorScheme) private var colorScheme

    private var headerOverlay: some View {
        let toolbarHeight: CGFloat = 56
        let searchBarPadding: CGFloat = theme.spacing.xxSmall * 2
        let searchBarFieldHeight: CGFloat = 40
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        let totalHeight = toolbarHeight + safeAreaTop + searchBarFieldHeight + searchBarPadding
        let blurStyle: UIBlurEffect.Style = colorScheme == .dark ? .systemThinMaterialDark : .systemThinMaterialLight

        return ZStack(alignment: .top) {
            theme.colors.surface
                .frame(height: totalHeight)
                .ignoresSafeArea()
                .allowsHitTesting(false)

            VisualEffectView(effect: UIBlurEffect(style: blurStyle))
                .frame(height: totalHeight)
                .opacity(0.8)
                .ignoresSafeArea()
                .allowsHitTesting(false)

            VStack(spacing: 0) {
                searchToolbar
                searchBar
                    .padding(.horizontal, theme.spacing.medium)
                    .padding(.vertical, theme.spacing.xxSmall)
            }
        }
        .frame(maxWidth: .infinity)
    }

    private var searchToolbar: some View {
        GlassToolbar(
            title: title,
            opacity: 1.0,
            leadingIcon: {
                GlassButton(icon: "chevron.left", action: onBack)
            },
            trailingIcon: {
                if isBrowsingGenres {
                    GlassButton(icon: "line.3.horizontal.decrease") {
                        showFilterSheet = true
                    }
                } else {
                    Rectangle()
                        .fill(Color.clear)
                        .frame(width: 44)
                }
            }
        )
    }

    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(theme.colors.onSurfaceVariant)

            TextField(searchPlaceholder, text: $query)
                .textStyle(theme.typography.bodyMedium)
                .focused($isSearchFocused)
                .submitLabel(.search)

            if !query.isEmpty {
                Button {
                    query = ""
                } label: {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }
            }
        }
        .padding(.horizontal, theme.spacing.small)
        .padding(.vertical, 10)
        .background(theme.colors.surfaceVariant.opacity(0.8))
        .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
        .overlay(
            RoundedRectangle(cornerRadius: theme.shapes.medium)
                .strokeBorder(theme.colors.onSurface.opacity(0.15), lineWidth: 1)
        )
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

    private var totalHeaderHeight: CGFloat {
        let toolbarHeight: CGFloat = 56
        let searchBarHeight: CGFloat = 44
        let safeAreaTop = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
        return toolbarHeight + safeAreaTop + searchBarHeight + 16
    }
}
