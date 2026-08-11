import Components
import DesignSystem
import Models
import SwiftUI

public struct LibraryScreen: View {
    public struct State: Equatable {
        public let title: String
        public let searchPlaceholder: String
        public let emptyText: String
        public let isLoading: Bool
        public let isRefreshing: Bool
        public let isEmpty: Bool
        public let layout: SwiftListStyle
        public let isLayoutLocked: Bool
        public let layoutMenuCopy: LayoutMenuCopy
        public let isSearchActive: Bool
        public let query: String
        public let gridItems: [LibraryGridItem]
        public let listItems: [SwiftLibraryItem]

        public init(
            title: String,
            searchPlaceholder: String,
            emptyText: String,
            isLoading: Bool,
            isRefreshing: Bool,
            isEmpty: Bool,
            layout: SwiftListStyle,
            isLayoutLocked: Bool,
            layoutMenuCopy: LayoutMenuCopy,
            isSearchActive: Bool,
            query: String,
            gridItems: [LibraryGridItem],
            listItems: [SwiftLibraryItem]
        ) {
            self.title = title
            self.searchPlaceholder = searchPlaceholder
            self.emptyText = emptyText
            self.isLoading = isLoading
            self.isRefreshing = isRefreshing
            self.isEmpty = isEmpty
            self.layout = layout
            self.isLayoutLocked = isLayoutLocked
            self.layoutMenuCopy = layoutMenuCopy
            self.isSearchActive = isSearchActive
            self.query = query
            self.gridItems = gridItems
            self.listItems = listItems
        }
    }

    @Environment(\.appTheme) private var appTheme
    @Environment(\.widthSizeClass) private var widthSizeClass

    private let state: State
    private let emptySearchResultFormat: ((String) -> String)?
    private let onQueryChanged: (String) -> Void
    private let onQueryCleared: () -> Void
    private let onLayoutSelected: (SwiftListStyle) -> Void
    private let onUpgradeRequested: () -> Void
    private let onToggleSearch: () -> Void
    private let onSortClicked: () -> Void
    private let onShowClicked: (Int64) -> Void

    public init(
        state: State,
        emptySearchResultFormat: ((String) -> String)? = nil,
        onQueryChanged: @escaping (String) -> Void,
        onQueryCleared: @escaping () -> Void,
        onLayoutSelected: @escaping (SwiftListStyle) -> Void,
        onUpgradeRequested: @escaping () -> Void,
        onToggleSearch: @escaping () -> Void,
        onSortClicked: @escaping () -> Void,
        onShowClicked: @escaping (Int64) -> Void
    ) {
        self.state = state
        self.emptySearchResultFormat = emptySearchResultFormat
        self.onQueryChanged = onQueryChanged
        self.onQueryCleared = onQueryCleared
        self.onLayoutSelected = onLayoutSelected
        self.onUpgradeRequested = onUpgradeRequested
        self.onToggleSearch = onToggleSearch
        self.onSortClicked = onSortClicked
        self.onShowClicked = onShowClicked
    }

    @FocusState private var isSearchFocused: Bool
    @Namespace private var animation
    @SwiftUI.State private var localQuery: String = ""

    public var body: some View {
        ZStack {
            VStack {
                contentView
            }
            .padding(.top, toolbarInset)
        }
        .appScreen()
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarColor(backgroundColor: .clear)
        .disableAutocorrection(true)
        .overlay(
            Group {
                if state.isSearchActive {
                    searchBarOverlay
                } else {
                    libraryToolbar
                }
            },
            alignment: .top
        )
        .edgesIgnoringSafeArea(.top)
        .animation(.spring(response: 0.3, dampingFraction: 0.8), value: state.isSearchActive)
        .disableAutocorrection(true)
        .textInputAutocapitalization(.never)
        .onAppear {
            localQuery = state.query
        }
        .onChange(of: state.query) { _, newValue in
            localQuery = newValue
        }
    }

    @ViewBuilder
    private var contentView: some View {
        if state.isLoading {
            CenteredFullScreenView {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: appTheme.colors.accent))
                    .scaleEffect(1.5)
            }
        } else if state.isEmpty {
            emptyView
        } else if state.layout == .grid {
            gridContent
        } else if state.layout == .list {
            listContent
        } else if state.layout == .compact {
            compactContent
        } else {
            detailedContent
        }
    }

    private var libraryToolbar: some View {
        GlassToolbar(
            title: state.title,
            opacity: 1.0,
            isLoading: state.isRefreshing,
            leadingIcon: {
                layoutMenu
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

    private var layoutMenu: some View {
        let copy = state.layoutMenuCopy
        return Menu {
            layoutMenuRow(.grid, copy: copy)
            layoutMenuRow(.list, copy: copy)
            Section(copy.premiumSectionTitle) {
                layoutMenuRow(.compact, copy: copy)
                layoutMenuRow(.detailed, copy: copy)
            }
        } label: {
            GlassButton(icon: icon(for: state.layout), action: {})
        }
    }

    @ViewBuilder
    private func layoutMenuRow(_ layout: SwiftListStyle, copy: LayoutMenuCopy) -> some View {
        let label = copy.label(for: layout)
        if layout.isPremium, state.isLayoutLocked {
            Button {
                onUpgradeRequested()
            } label: {
                Label(label, systemImage: "lock.fill")
            }
            .accessibilityLabel("\(label), \(copy.lockedAccessibilitySuffix)")
            .accessibilityHint(copy.lockedHint)
            .accessibilityAction(named: Text(copy.upgradeActionName)) {
                onUpgradeRequested()
            }
        } else {
            Button(label, systemImage: icon(for: layout)) {
                withAnimation { onLayoutSelected(layout) }
            }
            .accessibilityAddTraits(layout == state.layout ? .isSelected : [])
        }
    }

    private func icon(for layout: SwiftListStyle) -> String {
        switch layout {
        case .grid: "rectangle.grid.2x2"
        case .list: "list.bullet"
        case .compact: "rectangle.compress.vertical"
        case .detailed: "rectangle.expand.vertical"
        }
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
                    .foregroundStyle(.appOnSurfaceVariant)

                TextField(state.searchPlaceholder, text: $localQuery)
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
                        .foregroundStyle(.appOnSurfaceVariant)
                }
            }
            .padding(.horizontal, appTheme.spacing.small)
            .padding(.vertical, appTheme.spacing.xxSmall)
            .background(.appSurfaceVariant.opacity(0.5))
            .clipShape(RoundedRectangle(cornerRadius: appTheme.shapes.medium))
        }
        .frame(maxWidth: .infinity)
    }

    private var gridContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVGrid(
                columns: ImageDimens.posterGridColumns(widthSizeClass, spacing: ImageDimens.gridItemSpacing),
                spacing: ImageDimens.gridItemSpacing
            ) {
                ForEach(state.gridItems) { item in
                    PosterItemView(
                        title: item.title,
                        posterUrl: item.posterImageUrl
                    )
                    .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                    .clipped()
                    .matchedGeometryEffect(id: item.showId, in: animation)
                    .onTapGesture {
                        onShowClicked(item.showId)
                    }
                }
            }
            .padding(.horizontal, appTheme.spacing.xSmall)
            .padding(.top, appTheme.spacing.large)
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: state.layout)
    }

    private var listContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(spacing: appTheme.spacing.small) {
                ForEach(state.listItems) { item in
                    LibraryListItemView(
                        item: item,
                        onItemClicked: {
                            onShowClicked(item.showId)
                        }
                    )
                }
            }
            .padding(.horizontal, appTheme.spacing.xSmall)
            .padding(.top, appTheme.spacing.large)
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: state.layout)
    }

    private var compactContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(spacing: appTheme.spacing.small) {
                ForEach(state.listItems) { item in
                    LibraryCompactRowView(
                        item: item,
                        onItemClicked: {
                            onShowClicked(item.showId)
                        }
                    )
                }
            }
            .padding(.horizontal, appTheme.spacing.small)
            .padding(.top, appTheme.spacing.large)
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: state.layout)
    }

    private var detailedContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(spacing: appTheme.spacing.small) {
                ForEach(state.listItems) { item in
                    LibraryDetailedCardView(
                        item: item,
                        onItemClicked: {
                            onShowClicked(item.showId)
                        }
                    )
                }
            }
            .padding(.horizontal, appTheme.spacing.small)
            .padding(.top, appTheme.spacing.large)
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: state.layout)
    }

    @ViewBuilder
    private var emptyView: some View {
        let subtitle = state.query.isEmpty ? nil : emptySearchResultFormat?(state.query)

        EmptyStateView(
            title: state.emptyText,
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
        showId
    }

    public let showId: Int64
    public let title: String
    public let posterImageUrl: String?

    public init(showId: Int64, title: String, posterImageUrl: String?) {
        self.showId = showId
        self.title = title
        self.posterImageUrl = posterImageUrl
    }
}
