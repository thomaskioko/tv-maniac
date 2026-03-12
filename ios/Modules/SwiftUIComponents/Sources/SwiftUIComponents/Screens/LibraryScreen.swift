import SwiftUI

public struct LibraryScreen: View {
    @Theme private var appTheme

    private let title: String
    private let searchPlaceholder: String
    private let emptyText: String
    private let isLoading: Bool
    private let isRefreshing: Bool
    private let isEmpty: Bool
    private let isGridMode: Bool
    private let isSearchActive: Bool
    private let query: String
    private let gridItems: [LibraryGridItem]
    private let listItems: [SwiftLibraryItem]
    private let emptySearchResultFormat: ((String) -> String)?
    private let onQueryChanged: (String) -> Void
    private let onQueryCleared: () -> Void
    private let onToggleListStyle: () -> Void
    private let onToggleSearch: () -> Void
    private let onSortClicked: () -> Void
    private let onShowClicked: (Int64) -> Void

    public init(
        title: String,
        searchPlaceholder: String,
        emptyText: String,
        isLoading: Bool,
        isRefreshing: Bool,
        isEmpty: Bool,
        isGridMode: Bool,
        isSearchActive: Bool,
        query: String,
        gridItems: [LibraryGridItem],
        listItems: [SwiftLibraryItem],
        emptySearchResultFormat: ((String) -> String)? = nil,
        onQueryChanged: @escaping (String) -> Void,
        onQueryCleared: @escaping () -> Void,
        onToggleListStyle: @escaping () -> Void,
        onToggleSearch: @escaping () -> Void,
        onSortClicked: @escaping () -> Void,
        onShowClicked: @escaping (Int64) -> Void
    ) {
        self.title = title
        self.searchPlaceholder = searchPlaceholder
        self.emptyText = emptyText
        self.isLoading = isLoading
        self.isRefreshing = isRefreshing
        self.isEmpty = isEmpty
        self.isGridMode = isGridMode
        self.isSearchActive = isSearchActive
        self.query = query
        self.gridItems = gridItems
        self.listItems = listItems
        self.emptySearchResultFormat = emptySearchResultFormat
        self.onQueryChanged = onQueryChanged
        self.onQueryCleared = onQueryCleared
        self.onToggleListStyle = onToggleListStyle
        self.onToggleSearch = onToggleSearch
        self.onSortClicked = onSortClicked
        self.onShowClicked = onShowClicked
    }

    @FocusState private var isSearchFocused: Bool
    @Namespace private var animation
    @State private var localQuery: String = ""

    public var body: some View {
        ZStack {
            appTheme.colors.background
                .ignoresSafeArea()

            VStack {
                contentView
            }
            .padding(.top, toolbarInset)
        }
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .disableAutocorrection(true)
        .overlay(
            Group {
                if isSearchActive {
                    searchBarOverlay
                } else {
                    libraryToolbar
                }
            },
            alignment: .top
        )
        .edgesIgnoringSafeArea(.top)
        .animation(.spring(response: 0.3, dampingFraction: 0.8), value: isSearchActive)
        .disableAutocorrection(true)
        .textInputAutocapitalization(.never)
        .onAppear {
            localQuery = query
        }
        .onChange(of: query) { _, newValue in
            localQuery = newValue
        }
    }

    @ViewBuilder
    private var contentView: some View {
        if isLoading {
            CenteredFullScreenView {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: appTheme.colors.accent))
                    .scaleEffect(1.5)
            }
        } else if isEmpty {
            emptyView
        } else if isGridMode {
            gridContent
        } else {
            listContent
        }
    }

    private var libraryToolbar: some View {
        let image = isGridMode ? "list.bullet" : "rectangle.grid.2x2"
        return GlassToolbar(
            title: title,
            opacity: 1.0,
            isLoading: isRefreshing,
            leadingIcon: {
                GlassButton(icon: image) {
                    withAnimation {
                        onToggleListStyle()
                    }
                }
            },
            trailingIcon: {
                HStack(spacing: appTheme.spacing.xSmall) {
                    GlassButton(icon: "magnifyingglass") {
                        withAnimation(.spring(response: 0.3, dampingFraction: 0.8)) {
                            onToggleSearch()
                            isSearchFocused = true
                        }
                    }
                    GlassButton(icon: "line.3.horizontal.decrease.circle", action: onSortClicked)
                }
            }
        )
    }

    private var searchBarOverlay: some View {
        let topPadding = safeAreaTop
        return ZStack(alignment: .top) {
            appTheme.colors.surface
                .frame(height: toolbarHeight + topPadding)
                .ignoresSafeArea()

            expandedSearchBar
                .padding(.horizontal, appTheme.spacing.medium)
                .padding(.top, topPadding + appTheme.spacing.xSmall)
        }
        .frame(maxWidth: .infinity)
    }

    private var expandedSearchBar: some View {
        HStack(spacing: appTheme.spacing.small) {
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(appTheme.colors.onSurfaceVariant)

                TextField(searchPlaceholder, text: $localQuery)
                    .textStyle(appTheme.typography.bodyMedium)
                    .focused($isSearchFocused)
                    .submitLabel(.search)
                    .onChange(of: localQuery) { _, newValue in
                        onQueryChanged(newValue)
                    }

                Button {
                    withAnimation(.spring(response: 0.3, dampingFraction: 0.8)) {
                        if !localQuery.isEmpty {
                            localQuery = ""
                            onQueryCleared()
                        } else {
                            onToggleSearch()
                            isSearchFocused = false
                        }
                    }
                } label: {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(appTheme.colors.onSurfaceVariant)
                }
            }
            .padding(.horizontal, appTheme.spacing.small)
            .padding(.vertical, 6)
            .background(appTheme.colors.surfaceVariant.opacity(0.5))
            .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.medium))
        }
        .frame(maxWidth: .infinity)
    }

    private var gridContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVGrid(columns: LibraryScreenConstants.columns, spacing: LibraryScreenConstants.spacing) {
                ForEach(gridItems) { item in
                    PosterItemView(
                        title: item.title,
                        posterUrl: item.posterImageUrl
                    )
                    .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                    .clipped()
                    .matchedGeometryEffect(id: item.traktId, in: animation)
                    .onTapGesture {
                        onShowClicked(item.traktId)
                    }
                }
            }
            .padding(.horizontal, appTheme.spacing.xSmall)
            .padding(.top, appTheme.spacing.large)
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: isGridMode)
    }

    private var listContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(spacing: appTheme.spacing.small) {
                ForEach(listItems) { item in
                    LibraryListItemView(
                        item: item,
                        onItemClicked: {
                            onShowClicked(item.traktId)
                        }
                    )
                }
            }
            .padding(.horizontal, appTheme.spacing.xSmall)
            .padding(.top, appTheme.spacing.large)
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: isGridMode)
    }

    @ViewBuilder
    private var emptyView: some View {
        let subtitle = query.isEmpty ? nil : emptySearchResultFormat?(query)

        EmptyStateView(
            title: emptyText,
            message: subtitle
        )
    }

    private let toolbarHeight: CGFloat = 44

    private var safeAreaTop: CGFloat {
        (UIApplication.shared.connectedScenes.first as? UIWindowScene)?
            .windows.first?.safeAreaInsets.top ?? 0
    }

    private var toolbarInset: CGFloat {
        toolbarHeight + safeAreaTop
    }
}

public struct LibraryGridItem: Identifiable, Equatable {
    public var id: Int64 {
        traktId
    }

    public let traktId: Int64
    public let title: String
    public let posterImageUrl: String?

    public init(traktId: Int64, title: String, posterImageUrl: String?) {
        self.traktId = traktId
        self.title = title
        self.posterImageUrl = posterImageUrl
    }
}

private enum LibraryScreenConstants {
    static let spacing: CGFloat = 4
    static let columns: [GridItem] = [
        GridItem(.adaptive(minimum: 100), spacing: spacing),
    ]
}
