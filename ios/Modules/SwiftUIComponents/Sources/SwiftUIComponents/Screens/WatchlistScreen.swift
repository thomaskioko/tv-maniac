import SwiftUI

public struct WatchlistScreen: View {
    @Theme private var appTheme

    private let title: String
    private let searchPlaceholder: String
    private let emptyText: String
    private let upToDateText: String
    private let listStyleLabel: String
    private let searchLabel: String
    private let sortLabel: String
    private let upNextSectionTitle: String
    private let staleSectionTitle: String
    private let premiereLabel: String
    private let newLabel: String
    private let isLoading: Bool
    private let isGridMode: Bool
    private let isSearchActive: Bool
    private let query: String
    private let watchNextGridItems: [WatchlistGridItem]
    private let staleGridItems: [WatchlistGridItem]
    private let watchNextEpisodes: [SwiftNextEpisode]
    private let staleEpisodes: [SwiftNextEpisode]
    private let onQueryChanged: (String) -> Void
    private let onQueryCleared: () -> Void
    private let onToggleListStyle: () -> Void
    private let onToggleSearch: () -> Void
    private let onShowClicked: (Int64) -> Void
    private let onEpisodeClicked: (Int64, Int64) -> Void
    private let onShowTitleClicked: (Int64) -> Void
    private let onMarkWatched: (SwiftNextEpisode) -> Void

    public init(
        title: String,
        searchPlaceholder: String,
        emptyText: String,
        upToDateText: String,
        listStyleLabel: String,
        searchLabel: String,
        sortLabel: String,
        upNextSectionTitle: String,
        staleSectionTitle: String,
        premiereLabel: String,
        newLabel: String,
        isLoading: Bool,
        isGridMode: Bool,
        isSearchActive: Bool,
        query: String,
        watchNextGridItems: [WatchlistGridItem],
        staleGridItems: [WatchlistGridItem],
        watchNextEpisodes: [SwiftNextEpisode],
        staleEpisodes: [SwiftNextEpisode],
        onQueryChanged: @escaping (String) -> Void,
        onQueryCleared: @escaping () -> Void,
        onToggleListStyle: @escaping () -> Void,
        onToggleSearch: @escaping () -> Void,
        onShowClicked: @escaping (Int64) -> Void,
        onEpisodeClicked: @escaping (Int64, Int64) -> Void,
        onShowTitleClicked: @escaping (Int64) -> Void,
        onMarkWatched: @escaping (SwiftNextEpisode) -> Void
    ) {
        self.title = title
        self.searchPlaceholder = searchPlaceholder
        self.emptyText = emptyText
        self.upToDateText = upToDateText
        self.listStyleLabel = listStyleLabel
        self.searchLabel = searchLabel
        self.sortLabel = sortLabel
        self.upNextSectionTitle = upNextSectionTitle
        self.staleSectionTitle = staleSectionTitle
        self.premiereLabel = premiereLabel
        self.newLabel = newLabel
        self.isLoading = isLoading
        self.isGridMode = isGridMode
        self.isSearchActive = isSearchActive
        self.query = query
        self.watchNextGridItems = watchNextGridItems
        self.staleGridItems = staleGridItems
        self.watchNextEpisodes = watchNextEpisodes
        self.staleEpisodes = staleEpisodes
        self.onQueryChanged = onQueryChanged
        self.onQueryCleared = onQueryCleared
        self.onToggleListStyle = onToggleListStyle
        self.onToggleSearch = onToggleSearch
        self.onShowClicked = onShowClicked
        self.onEpisodeClicked = onEpisodeClicked
        self.onShowTitleClicked = onShowTitleClicked
        self.onMarkWatched = onMarkWatched
    }

    @State private var showListSelection = false
    @State private var isRotating = 0.0
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
        }
        .navigationBarTitleDisplayMode(.inline)
        .disableAutocorrection(true)
        .toolbar {
            if isSearchActive {
                ToolbarItem(placement: .principal) {
                    expandedSearchBar
                }
            } else {
                let image = isGridMode ? "list.bullet" : "rectangle.grid.2x2"
                ToolbarItem(placement: .navigationBarLeading) {
                    HStack {
                        Button {
                            withAnimation { onToggleListStyle() }
                        } label: {
                            Label(listStyleLabel, systemImage: image)
                                .labelStyle(.iconOnly)
                        }
                        .buttonBorderShape(.roundedRectangle(radius: appTheme.shapes.large))
                        .buttonStyle(.bordered)
                        .tint(appTheme.colors.accent)
                    }
                }
                ToolbarItem(placement: .principal) {
                    titleView
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    HStack(spacing: appTheme.spacing.xSmall) {
                        searchButton
                        filterButton
                    }
                }
            }
        }
        .animation(.spring(response: 0.3, dampingFraction: 0.8), value: isSearchActive)
        .disableAutocorrection(true)
        .textInputAutocapitalization(.never)
        .toolbarBackground(appTheme.colors.surface, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
        .onAppear { localQuery = query }
        .onChange(of: query) { _, newValue in localQuery = newValue }
    }

    @ViewBuilder
    private var contentView: some View {
        let hasNoGridItems = watchNextGridItems.isEmpty && staleGridItems.isEmpty
        let hasNoEpisodes = watchNextEpisodes.isEmpty && staleEpisodes.isEmpty

        if isLoading {
            CenteredFullScreenView {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: appTheme.colors.accent))
                    .scaleEffect(1.5)
            }
        } else if isGridMode {
            if hasNoGridItems {
                gridEmptyView
            } else {
                sectionedGridContent
            }
        } else {
            if hasNoEpisodes {
                upNextEmptyView
            } else {
                sectionedListContent
            }
        }
    }

    private var titleView: some View {
        HStack {
            Text(title)
                .textStyle(appTheme.typography.titleMedium)
                .lineLimit(1)
                .foregroundColor(appTheme.colors.onSurface)
            Button {
                withAnimation { showListSelection.toggle() }
            } label: {
                Image(systemName: "chevron.down.circle.fill")
                    .textStyle(appTheme.typography.labelSmall)
                    .foregroundColor(appTheme.colors.onSurfaceVariant)
                    .rotationEffect(.degrees(isRotating))
                    .task(id: showListSelection) {
                        withAnimation(.easeInOut) {
                            isRotating = showListSelection ? -180.0 : 0.0
                        }
                    }
            }
        }
    }

    private var searchButton: some View {
        Button {
            withAnimation(.spring(response: 0.3, dampingFraction: 0.8)) {
                onToggleSearch()
                isSearchFocused = true
            }
        } label: {
            Label(searchLabel, systemImage: "magnifyingglass")
                .labelStyle(.iconOnly)
        }
        .buttonBorderShape(.roundedRectangle(radius: appTheme.shapes.large))
        .buttonStyle(.bordered)
        .tint(appTheme.colors.accent)
    }

    private var filterButton: some View {
        Button {
            withAnimation {}
        } label: {
            Label(sortLabel, systemImage: "line.3.horizontal.decrease.circle")
                .labelStyle(.iconOnly)
        }
        .buttonBorderShape(.roundedRectangle(radius: appTheme.shapes.large))
        .buttonStyle(.bordered)
        .tint(appTheme.colors.accent)
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
        .padding(.bottom, appTheme.spacing.small)
        .transition(.scale.combined(with: .opacity))
    }

    private var sectionedListContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(spacing: appTheme.spacing.xSmall, pinnedViews: [.sectionHeaders]) {
                if !watchNextEpisodes.isEmpty {
                    Section {
                        ForEach(watchNextEpisodes, id: \.episodeId) { episode in
                            WatchListItemView(
                                episode: episode,
                                premiereLabel: premiereLabel,
                                newLabel: newLabel,
                                onItemClicked: onEpisodeClicked,
                                onShowTitleClicked: onShowTitleClicked,
                                onMarkWatched: { onMarkWatched(episode) }
                            )
                            .transition(
                                .asymmetric(
                                    insertion: .scale(scale: 0.9).combined(with: .opacity),
                                    removal: .scale(scale: 1.1).combined(with: .opacity)
                                )
                            )
                        }
                    } header: {
                        SectionHeaderView(title: upNextSectionTitle)
                    }
                }

                if !staleEpisodes.isEmpty {
                    Section {
                        ForEach(staleEpisodes, id: \.episodeId) { episode in
                            WatchListItemView(
                                episode: episode,
                                premiereLabel: premiereLabel,
                                newLabel: newLabel,
                                onItemClicked: onEpisodeClicked,
                                onShowTitleClicked: onShowTitleClicked,
                                onMarkWatched: { onMarkWatched(episode) }
                            )
                            .transition(
                                .asymmetric(
                                    insertion: .scale(scale: 0.9).combined(with: .opacity),
                                    removal: .scale(scale: 1.1).combined(with: .opacity)
                                )
                            )
                        }
                    } header: {
                        SectionHeaderView(title: staleSectionTitle)
                    }
                }
            }
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: isGridMode)
    }

    private var sectionedGridContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(spacing: appTheme.spacing.small, pinnedViews: [.sectionHeaders]) {
                if !watchNextGridItems.isEmpty {
                    Section {
                        gridItemsView(items: watchNextGridItems)
                    } header: {
                        SectionHeaderView(title: upNextSectionTitle)
                    }
                }

                if !staleGridItems.isEmpty {
                    Section {
                        gridItemsView(items: staleGridItems)
                    } header: {
                        SectionHeaderView(title: staleSectionTitle)
                    }
                }
            }
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: isGridMode)
    }

    private func gridItemsView(items: [WatchlistGridItem]) -> some View {
        LazyVGrid(columns: WatchlistScreenConstants.columns, spacing: WatchlistScreenConstants.spacing) {
            ForEach(items) { item in
                ZStack(alignment: .bottom) {
                    PosterItemView(
                        title: item.title,
                        posterUrl: item.posterImageUrl
                    )

                    ProgressView(value: item.watchProgress, total: 1)
                        .progressViewStyle(RoundedRectProgressViewStyle())
                        .offset(y: 2)
                }
                .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                .clipped()
                .matchedGeometryEffect(id: item.traktId, in: animation)
                .onTapGesture {
                    onShowClicked(item.traktId)
                }
            }
        }
        .padding(.horizontal)
    }

    @ViewBuilder
    private var gridEmptyView: some View {
        let subtitle = query.isEmpty ? nil : "\(emptyText) \"\(query)\""

        EmptyStateView(
            title: emptyText,
            message: subtitle
        )
    }

    private var upNextEmptyView: some View {
        EmptyStateView(
            systemName: "checkmark.circle",
            title: upToDateText
        )
    }
}

public struct WatchlistGridItem: Identifiable, Equatable {
    public var id: Int64 {
        traktId
    }

    public let traktId: Int64
    public let title: String
    public let posterImageUrl: String?
    public let watchProgress: Float

    public init(traktId: Int64, title: String, posterImageUrl: String?, watchProgress: Float) {
        self.traktId = traktId
        self.title = title
        self.posterImageUrl = posterImageUrl
        self.watchProgress = watchProgress
    }
}

private enum WatchlistScreenConstants {
    static let spacing: CGFloat = 4
    static let columns: [GridItem] = [
        GridItem(.adaptive(minimum: 100), spacing: spacing),
    ]
}
