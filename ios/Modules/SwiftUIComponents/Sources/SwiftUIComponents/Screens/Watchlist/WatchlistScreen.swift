import DesignSystem
import SwiftUI

public struct WatchlistScreen: View {
    public struct State {
        public let title: String
        public let searchPlaceholder: String
        public let emptyText: String
        public let upToDateText: String
        public let listStyleLabel: String
        public let searchLabel: String
        public let sortLabel: String
        public let upNextSectionTitle: String
        public let staleSectionTitle: String
        public let premiereLabel: String
        public let newLabel: String
        public let isLoading: Bool
        public let isGridMode: Bool
        public let isSearchActive: Bool
        public let query: String
        public let watchNextGridItems: [WatchlistGridItem]
        public let staleGridItems: [WatchlistGridItem]
        public let watchNextEpisodes: [SwiftNextEpisode]
        public let staleEpisodes: [SwiftNextEpisode]

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
            staleEpisodes: [SwiftNextEpisode]
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
        }
    }

    @Environment(\.appTheme) private var appTheme

    private let state: State
    private let onQueryChanged: (String) -> Void
    private let onQueryCleared: () -> Void
    private let onToggleListStyle: () -> Void
    private let onToggleSearch: () -> Void
    private let onShowClicked: (Int64) -> Void
    private let onEpisodeClicked: (Int64, Int64) -> Void
    private let onShowTitleClicked: (Int64) -> Void
    private let onMarkWatched: (SwiftNextEpisode) -> Void

    public init(
        state: State,
        onQueryChanged: @escaping (String) -> Void,
        onQueryCleared: @escaping () -> Void,
        onToggleListStyle: @escaping () -> Void,
        onToggleSearch: @escaping () -> Void,
        onShowClicked: @escaping (Int64) -> Void,
        onEpisodeClicked: @escaping (Int64, Int64) -> Void,
        onShowTitleClicked: @escaping (Int64) -> Void,
        onMarkWatched: @escaping (SwiftNextEpisode) -> Void
    ) {
        self.state = state
        self.onQueryChanged = onQueryChanged
        self.onQueryCleared = onQueryCleared
        self.onToggleListStyle = onToggleListStyle
        self.onToggleSearch = onToggleSearch
        self.onShowClicked = onShowClicked
        self.onEpisodeClicked = onEpisodeClicked
        self.onShowTitleClicked = onShowTitleClicked
        self.onMarkWatched = onMarkWatched
    }

    @SwiftUI.State private var showListSelection = false
    @SwiftUI.State private var isRotating = 0.0
    @FocusState private var isSearchFocused: Bool
    @Namespace private var animation
    @SwiftUI.State private var localQuery: String = ""

    public var body: some View {
        ZStack {
            VStack {
                contentView
            }
        }
        .appScreen()
        .navigationBarTitleDisplayMode(.inline)
        .disableAutocorrection(true)
        .toolbar {
            if state.isSearchActive {
                ToolbarItem(placement: .principal) {
                    expandedSearchBar
                }
            } else {
                let image = state.isGridMode ? "list.bullet" : "rectangle.grid.2x2"
                ToolbarItem(placement: .navigationBarLeading) {
                    HStack {
                        Button {
                            withAnimation { onToggleListStyle() }
                        } label: {
                            Label(state.listStyleLabel, systemImage: image)
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
        .animation(.spring(response: 0.3, dampingFraction: 0.8), value: state.isSearchActive)
        .disableAutocorrection(true)
        .textInputAutocapitalization(.never)
        .toolbarBackground(.appSurface, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
        .onAppear { localQuery = state.query }
        .onChange(of: state.query) { _, newValue in localQuery = newValue }
    }

    @ViewBuilder
    private var contentView: some View {
        let hasNoGridItems = state.watchNextGridItems.isEmpty && state.staleGridItems.isEmpty
        let hasNoEpisodes = state.watchNextEpisodes.isEmpty && state.staleEpisodes.isEmpty

        if state.isLoading {
            CenteredFullScreenView {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: appTheme.colors.accent))
                    .scaleEffect(1.5)
            }
        } else if state.isGridMode {
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
            Text(state.title)
                .textStyle(appTheme.typography.titleMedium)
                .lineLimit(1)
                .foregroundStyle(.appOnSurface)
            Button {
                withAnimation { showListSelection.toggle() }
            } label: {
                Image(systemName: "chevron.down.circle.fill")
                    .textStyle(appTheme.typography.labelSmall)
                    .foregroundStyle(.appOnSurfaceVariant)
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
            Label(state.searchLabel, systemImage: "magnifyingglass")
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
            Label(state.sortLabel, systemImage: "line.3.horizontal.decrease.circle")
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
        .padding(.bottom, appTheme.spacing.small)
        .transition(.scale.combined(with: .opacity))
    }

    private var sectionedListContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(spacing: appTheme.spacing.xSmall, pinnedViews: [.sectionHeaders]) {
                if !state.watchNextEpisodes.isEmpty {
                    Section {
                        ForEach(state.watchNextEpisodes, id: \.episodeId) { episode in
                            WatchListItemView(
                                episode: episode,
                                premiereLabel: state.premiereLabel,
                                newLabel: state.newLabel,
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
                        SectionHeaderView(title: state.upNextSectionTitle)
                    }
                }

                if !state.staleEpisodes.isEmpty {
                    Section {
                        ForEach(state.staleEpisodes, id: \.episodeId) { episode in
                            WatchListItemView(
                                episode: episode,
                                premiereLabel: state.premiereLabel,
                                newLabel: state.newLabel,
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
                        SectionHeaderView(title: state.staleSectionTitle)
                    }
                }
            }
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: state.isGridMode)
    }

    private var sectionedGridContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(spacing: appTheme.spacing.small, pinnedViews: [.sectionHeaders]) {
                if !state.watchNextGridItems.isEmpty {
                    Section {
                        gridItemsView(items: state.watchNextGridItems)
                    } header: {
                        SectionHeaderView(title: state.upNextSectionTitle)
                    }
                }

                if !state.staleGridItems.isEmpty {
                    Section {
                        gridItemsView(items: state.staleGridItems)
                    } header: {
                        SectionHeaderView(title: state.staleSectionTitle)
                    }
                }
            }
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: state.isGridMode)
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
        let subtitle = state.query.isEmpty ? nil : "\(state.emptyText) \"\(state.query)\""

        EmptyStateView(
            title: state.emptyText,
            message: subtitle
        )
    }

    private var upNextEmptyView: some View {
        EmptyStateView(
            systemName: "checkmark.circle",
            title: state.upToDateText
        )
    }
}
