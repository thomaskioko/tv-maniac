import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct WatchlistTab: View {
    @Theme private var theme

    private let presenter: WatchlistPresenter
    @StateObject @KotlinStateFlow private var uiState: WatchlistState
    @State private var showListSelection = false
    @State private var isRotating = 0.0
    @FocusState private var isSearchFocused: Bool
    @Namespace private var animation

    // Cache converted episodes to avoid recreating arrays on every render
    @State private var watchNextEpisodesSwift: [SwiftNextEpisode] = []
    @State private var staleEpisodesSwift: [SwiftNextEpisode] = []

    init(presenter: WatchlistPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
    }

    private var searchQueryBinding: Binding<String> {
        BindingFactories.searchQuery(
            get: { uiState.query },
            onChanged: { presenter.dispatch(action: WatchlistQueryChanged(query: $0)) },
            onCleared: { presenter.dispatch(action: ClearWatchlistQuery()) }
        )
    }

    var body: some View {
        ZStack {
            theme.colors.background
                .ignoresSafeArea()

            VStack {
                contentView
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .disableAutocorrection(true)
        .toolbar {
            if uiState.isSearchActive {
                ToolbarItem(placement: .principal) {
                    expandedSearchBar
                }
            } else {
                let image = if uiState.isGridMode {
                    "list.bullet"
                } else {
                    "rectangle.grid.2x2"
                }
                ToolbarItem(placement: .navigationBarLeading) {
                    HStack {
                        Button {
                            withAnimation {
                                presenter.dispatch(action: ChangeListStyleClicked_(isGridMode: uiState.isGridMode))
                            }
                        } label: {
                            Label(String(\.label_watchlist_list_style), systemImage: image)
                                .labelStyle(.iconOnly)
                        }
                        .buttonBorderShape(.roundedRectangle(radius: theme.shapes.large))
                        .buttonStyle(.bordered)
                        .tint(theme.colors.accent)
                    }
                }
                ToolbarItem(placement: .principal) {
                    titleView
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    HStack(spacing: theme.spacing.xSmall) {
                        searchButton
                        filterButton
                    }
                }
            }
        }
        .animation(.spring(response: 0.3, dampingFraction: 0.8), value: uiState.isSearchActive)
        .disableAutocorrection(true)
        .textInputAutocapitalization(.never)
        .toolbarBackground(theme.colors.surface, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
        .onChange(of: uiState.watchNextEpisodes) { _, newValue in
            watchNextEpisodesSwift = newValue.map { $0.toSwift() }
        }
        .onChange(of: uiState.staleEpisodes) { _, newValue in
            staleEpisodesSwift = newValue.map { $0.toSwift() }
        }
        .onAppear {
            // Initialize cached arrays
            watchNextEpisodesSwift = uiState.watchNextEpisodes.map { $0.toSwift() }
            staleEpisodesSwift = uiState.staleEpisodes.map { $0.toSwift() }
        }
    }

    @ViewBuilder
    private var contentView: some View {
        let hasNoItems = uiState.watchNextItems.isEmpty && uiState.staleItems.isEmpty
        let hasNoEpisodes = uiState.watchNextEpisodes.isEmpty && uiState.staleEpisodes.isEmpty

        if uiState.showLoading {
            CenteredFullScreenView {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: theme.colors.accent))
                    .scaleEffect(1.5)
            }
        } else if uiState.isGridMode {
            if hasNoItems {
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
            Text(String(\.label_tab_watchlist))
                .textStyle(theme.typography.titleMedium)
                .lineLimit(1)
                .foregroundColor(theme.colors.onSurface)
            Button {
                withAnimation {
                    showListSelection.toggle()
                }
            } label: {
                Image(systemName: "chevron.down.circle.fill")
                    .textStyle(theme.typography.labelSmall)
                    .foregroundColor(theme.colors.onSurfaceVariant)
                    .rotationEffect(.degrees(isRotating))
                    .task(id: showListSelection) {
                        withAnimation(.easeInOut) {
                            if showListSelection {
                                isRotating = -180.0
                            } else {
                                isRotating = 0.0
                            }
                        }
                    }
            }
        }
    }

    private var searchButton: some View {
        Button {
            withAnimation(.spring(response: 0.3, dampingFraction: 0.8)) {
                presenter.dispatch(action: ToggleSearchActive_())
                isSearchFocused = true
            }
        } label: {
            Label(String(\.label_tab_search), systemImage: "magnifyingglass")
                .labelStyle(.iconOnly)
        }
        .buttonBorderShape(.roundedRectangle(radius: theme.shapes.large))
        .buttonStyle(.bordered)
        .tint(theme.colors.accent)
    }

    private var filterButton: some View {
        Button {
            withAnimation {
                // TODO: Show Filter menu
            }
        } label: {
            Label(String(\.label_watchlist_sort_list), systemImage: "line.3.horizontal.decrease.circle")
                .labelStyle(.iconOnly)
        }
        .buttonBorderShape(.roundedRectangle(radius: theme.shapes.large))
        .buttonStyle(.bordered)
        .tint(theme.colors.accent)
    }

    private var expandedSearchBar: some View {
        HStack(spacing: theme.spacing.small) {
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(theme.colors.onSurfaceVariant)

                TextField(String(\.label_search_placeholder), text: searchQueryBinding)
                    .textStyle(theme.typography.bodyMedium)
                    .focused($isSearchFocused)
                    .submitLabel(.search)

                Button {
                    withAnimation(.spring(response: 0.3, dampingFraction: 0.8)) {
                        if !uiState.query.isEmpty {
                            presenter.dispatch(action: ClearWatchlistQuery())
                        } else {
                            presenter.dispatch(action: ToggleSearchActive_())
                            isSearchFocused = false
                        }
                    }
                } label: {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(theme.colors.onSurfaceVariant)
                }
            }
            .padding(.horizontal, theme.spacing.small)
            .padding(.vertical, 6)
            .background(theme.colors.surfaceVariant.opacity(0.5))
            .clipShape(RoundedRectangle(cornerRadius: theme.shapes.medium))
        }
        .frame(maxWidth: .infinity)
        .padding(.bottom, theme.spacing.small)
        .transition(.scale.combined(with: .opacity))
    }

    @ViewBuilder
    private var sectionedListContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(spacing: theme.spacing.xSmall, pinnedViews: [.sectionHeaders]) {
                if !watchNextEpisodesSwift.isEmpty {
                    Section {
                        ForEach(watchNextEpisodesSwift, id: \.episodeId) { episode in
                            WatchListItemView(
                                episode: episode,
                                premiereLabel: String(\.badge_premiere),
                                newLabel: String(\.badge_new),
                                onItemClicked: { showTraktId, episodeId in
                                    presenter.dispatch(action: UpNextEpisodeClicked(
                                        showTraktId: showTraktId,
                                        episodeId: episodeId
                                    ))
                                },
                                onShowTitleClicked: { showTraktId in
                                    presenter.dispatch(action: ShowTitleClicked(showTraktId: showTraktId))
                                },
                                onMarkWatched: {
                                    presenter.dispatch(action: MarkUpNextEpisodeWatched(
                                        showTraktId: episode.showTraktId,
                                        episodeId: episode.episodeId,
                                        seasonNumber: episode.seasonNumber,
                                        episodeNumber: episode.episodeNumberValue
                                    ))
                                }
                            )
                            .transition(
                                .asymmetric(
                                    insertion: .scale(scale: 0.9).combined(with: .opacity),
                                    removal: .scale(scale: 1.1).combined(with: .opacity)
                                )
                            )
                        }
                    } header: {
                        SectionHeaderView(title: String(\.label_discover_up_next))
                    }
                }

                if !staleEpisodesSwift.isEmpty {
                    Section {
                        ForEach(staleEpisodesSwift, id: \.episodeId) { episode in
                            WatchListItemView(
                                episode: episode,
                                premiereLabel: String(\.badge_premiere),
                                newLabel: String(\.badge_new),
                                onItemClicked: { showTraktId, episodeId in
                                    presenter.dispatch(action: UpNextEpisodeClicked(
                                        showTraktId: showTraktId,
                                        episodeId: episodeId
                                    ))
                                },
                                onShowTitleClicked: { showTraktId in
                                    presenter.dispatch(action: ShowTitleClicked(showTraktId: showTraktId))
                                },
                                onMarkWatched: {
                                    presenter.dispatch(action: MarkUpNextEpisodeWatched(
                                        showTraktId: episode.showTraktId,
                                        episodeId: episode.episodeId,
                                        seasonNumber: episode.seasonNumber,
                                        episodeNumber: episode.episodeNumberValue
                                    ))
                                }
                            )
                            .transition(
                                .asymmetric(
                                    insertion: .scale(scale: 0.9).combined(with: .opacity),
                                    removal: .scale(scale: 1.1).combined(with: .opacity)
                                )
                            )
                        }
                    } header: {
                        SectionHeaderView(title: String(\.title_not_watched_for_while))
                    }
                }
            }
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: uiState.isGridMode)
    }

    @ViewBuilder
    private var sectionedGridContent: some View {
        ScrollView(showsIndicators: false) {
            LazyVStack(spacing: theme.spacing.small, pinnedViews: [.sectionHeaders]) {
                if !uiState.watchNextItems.isEmpty {
                    Section {
                        gridItemsView(items: Array(uiState.watchNextItems))
                    } header: {
                        SectionHeaderView(title: String(\.label_discover_up_next))
                    }
                }

                if !uiState.staleItems.isEmpty {
                    Section {
                        gridItemsView(items: Array(uiState.staleItems))
                    } header: {
                        SectionHeaderView(title: String(\.title_not_watched_for_while))
                    }
                }
            }
        }
        .animation(.spring(response: 0.4, dampingFraction: 0.8), value: uiState.isGridMode)
    }

    @ViewBuilder
    private func gridItemsView(items: [WatchlistItem]) -> some View {
        LazyVGrid(columns: WatchlistConstants.columns, spacing: WatchlistConstants.spacing) {
            ForEach(items, id: \.traktId) { item in
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
                    presenter.dispatch(action: WatchlistShowClicked(traktId: item.traktId))
                }
            }
        }
        .padding(.horizontal)
    }

    @ViewBuilder
    private var gridEmptyView: some View {
        let subtitle = uiState.query.isEmpty ? nil : String(\.label_watchlist_empty_result, parameter: uiState.query)

        CenteredFullScreenView {
            FullScreenView(
                systemName: "tray",
                message: String(\.generic_empty_content),
                subtitle: subtitle,
                color: theme.colors.onSurfaceVariant
            )
            .frame(maxWidth: .infinity)
        }
    }

    @ViewBuilder
    private var upNextEmptyView: some View {
        CenteredFullScreenView {
            FullScreenView(
                systemName: "checkmark.circle",
                message: String(\.label_up_to_date),
                color: theme.colors.onSurfaceVariant
            )
            .frame(maxWidth: .infinity)
        }
    }
}

public enum WatchlistConstants {
    static let spacing: CGFloat = 4
    static let columns: [GridItem] = [
        GridItem(.adaptive(minimum: 100), spacing: spacing),
    ]
}
