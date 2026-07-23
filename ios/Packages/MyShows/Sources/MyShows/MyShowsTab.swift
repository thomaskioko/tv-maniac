import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

public struct MyShowsTab: View {
    private let presenter: MyShowsPresenter
    private let continueWatchingPresenter: ContinueWatchingPresenter
    private let startWatchingPresenter: StartWatchingPresenter

    @StateValue private var uiState: MyShowsState
    @StateValue private var continueWatchingState: ContinueWatchingState
    @StateValue private var startWatchingState: StartWatchingState

    @Environment(\.appTheme) private var appTheme
    @State private var watchNextEpisodesSwift: [SwiftNextEpisode] = []
    @State private var staleEpisodesSwift: [SwiftNextEpisode] = []
    @State private var toast: Toast?
    @State private var showSortOptions = false
    @State private var localQuery: String = ""
    @FocusState private var isSearchFocused: Bool

    public init(presenter: MyShowsPresenter) {
        self.presenter = presenter
        continueWatchingPresenter = presenter.continueWatchingPresenter
        startWatchingPresenter = presenter.startWatchingPresenter
        _uiState = .init(presenter.stateValue)
        _continueWatchingState = .init(presenter.continueWatchingPresenter.stateValue)
        _startWatchingState = .init(presenter.startWatchingPresenter.stateValue)
    }

    public var body: some View {
        VStack(spacing: 0) {
            pagePicker

            TabView(selection: Binding(
                get: { Int(uiState.selectedPage) },
                set: { presenter.dispatch(action: MyShowsActionSelectPage(index: Int32($0))) }
            )) {
                continueWatchingPage
                    .tag(0)

                startWatchingPage
                    .tag(1)
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
        }
        .appScreen()
        .navigationBarTitleDisplayMode(.inline)
        .toolbar { toolbarContent }
        .animation(.spring(response: 0.3, dampingFraction: 0.8), value: uiState.isSearchActive)
        .toolbarBackground(.appSurface, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
        .sheet(isPresented: $showSortOptions) {
            MyShowsSortOptionsSheet(
                selectedSortOption: uiState.sortOption,
                onSortOptionSelected: { sortOption in
                    presenter.dispatch(action: MyShowsActionChangeSortOption(sortOption: sortOption))
                }
            )
            .presentationDetents([.medium, .large])
        }
        .toastView(toast: $toast)
        .onChange(of: continueWatchingState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(type: .error, title: String(\.label_error), message: message.message)
                continueWatchingPresenter.dispatch(action: ContinueWatchingMessageShown(id: message.id))
            }
        }
        .onChange(of: startWatchingState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(type: .error, title: String(\.label_error), message: message.message)
                startWatchingPresenter.dispatch(action: StartWatchingMessageShown(id: message.id))
            }
        }
        .onChange(of: continueWatchingState.watchNextEpisodes) { _, newValue in
            watchNextEpisodesSwift = newValue.map { $0.toSwift() }
        }
        .onChange(of: continueWatchingState.staleEpisodes) { _, newValue in
            staleEpisodesSwift = newValue.map { $0.toSwift() }
        }
        .onChange(of: uiState.query) { _, newValue in
            localQuery = newValue
        }
        .onAppear {
            localQuery = uiState.query
            watchNextEpisodesSwift = continueWatchingState.watchNextEpisodes.map { $0.toSwift() }
            staleEpisodesSwift = continueWatchingState.staleEpisodes.map { $0.toSwift() }
        }
        .onDisappear {
            watchNextEpisodesSwift.removeAll()
            staleEpisodesSwift.removeAll()
        }
    }

    private var pagePicker: some View {
        Picker("", selection: Binding(
            get: { Int(uiState.selectedPage) },
            set: { presenter.dispatch(action: MyShowsActionSelectPage(index: Int32($0))) }
        )) {
            Text(uiState.continueWatchingTitle).tag(0)
            Text(uiState.startWatchingTitle).tag(1)
        }
        .pickerStyle(.segmented)
        .padding(.horizontal)
        .padding(.vertical, appTheme.spacing.xSmall)
    }

    @ToolbarContentBuilder
    private var toolbarContent: some ToolbarContent {
        if uiState.isSearchActive {
            ToolbarItem(placement: .principal) {
                expandedSearchBar
            }
        } else {
            if uiState.selectedPage == 0 {
                ToolbarItem(placement: .navigationBarLeading) {
                    let image = uiState.isGridMode ? "list.bullet" : "rectangle.grid.2x2"
                    GlassButton(icon: image) {
                        withAnimation {
                            presenter.dispatch(action: MyShowsActionChangeListStyle(isGridMode: uiState.isGridMode))
                        }
                    }
                }
            }
            ToolbarItem(placement: .principal) {
                HStack(spacing: appTheme.spacing.xSmall) {
                    Text(String(\.label_tab_my_shows))
                        .textStyle(appTheme.typography.titleMedium)
                        .lineLimit(1)
                        .foregroundStyle(.appOnSurface)

                    if uiState.showRefreshIndicator {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: appTheme.colors.accent))
                            .scaleEffect(0.7)
                    }
                }
            }
            ToolbarItem(placement: .navigationBarTrailing) {
                HStack(spacing: appTheme.spacing.xSmall) {
                    GlassButton(icon: "magnifyingglass") {
                        withAnimation(.spring(response: 0.3, dampingFraction: 0.8)) {
                            presenter.dispatch(action: MyShowsActionToggleSearch())
                            isSearchFocused = true
                        }
                    }
                    GlassButton(icon: "line.3.horizontal.decrease.circle") {
                        showSortOptions = true
                    }
                }
            }
        }
    }

    private var expandedSearchBar: some View {
        HStack(spacing: appTheme.spacing.small) {
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundStyle(.appOnSurfaceVariant)

                TextField(String(\.label_search_placeholder), text: $localQuery)
                    .textStyle(appTheme.typography.bodyMedium)
                    .focused($isSearchFocused)
                    .submitLabel(.search)
                    .disableAutocorrection(true)
                    .textInputAutocapitalization(.never)
                    .onChange(of: localQuery) { _, newValue in
                        presenter.dispatch(action: MyShowsActionQueryChanged(query: newValue))
                    }

                Button {
                    withAnimation(.spring(response: 0.3, dampingFraction: 0.8)) {
                        if !localQuery.isEmpty {
                            localQuery = ""
                            presenter.dispatch(action: MyShowsActionClearQuery())
                        } else {
                            presenter.dispatch(action: MyShowsActionToggleSearch())
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
        .transition(.scale.combined(with: .opacity))
    }

    private var continueWatchingPage: some View {
        MyShowsScreen(
            state: continueWatchingState.toState(
                watchNextEpisodes: watchNextEpisodesSwift,
                staleEpisodes: staleEpisodesSwift
            ),
            onShowClicked: { id in
                continueWatchingPresenter.dispatch(action: ContinueWatchingShowClicked(showId: id))
            },
            onEpisodeClicked: { showId, episodeId in
                continueWatchingPresenter.dispatch(action: UpNextEpisodeClicked(
                    showId: showId,
                    episodeId: episodeId
                ))
            },
            onShowTitleClicked: { showId in
                continueWatchingPresenter.dispatch(action: ShowTitleClicked(showId: showId))
            },
            onMarkWatched: { episode in
                continueWatchingPresenter.dispatch(action: MarkUpNextEpisodeWatched(
                    showId: episode.showId,
                    episodeId: episode.episodeId,
                    seasonNumber: episode.seasonNumber,
                    episodeNumber: episode.episodeNumberValue
                ))
            },
            onRefresh: { continueWatchingPresenter.dispatch(action: RefreshContinueWatching(forceRefresh: true)) }
        )
    }

    @ViewBuilder
    private var startWatchingPage: some View {
        if startWatchingState.showLoading {
            ProgressView()
                .scaleEffect(1.5)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if startWatchingState.isEmpty {
            EmptyStateView(
                systemName: "play.rectangle.on.rectangle",
                title: String(\.label_start_watching_empty)
            )
        } else {
            GridView(
                items: startWatchingState.items.map { $0.toSwift() },
                onAction: { showId in
                    startWatchingPresenter.dispatch(action: StartWatchingShowClicked(showId: showId))
                }
            )
            .refreshable {
                startWatchingPresenter.dispatch(action: RefreshStartWatching(forceRefresh: true))
            }
        }
    }
}

private extension ContinueWatchingState {
    func toState(
        watchNextEpisodes: [SwiftNextEpisode],
        staleEpisodes: [SwiftNextEpisode]
    ) -> MyShowsScreen.State {
        MyShowsScreen.State(
            emptyText: labels.emptyTitle,
            upToDateText: labels.upToDate,
            upNextSectionTitle: labels.watchingTitle,
            staleSectionTitle: labels.staleTitle,
            premiereLabel: labels.premiereBadge,
            newLabel: labels.newBadge,
            isLoading: showLoading,
            isGridMode: isGridMode,
            query: query,
            watchNextGridItems: Array(watchNextItems).map {
                MyShowsGridItem(
                    showId: $0.showId,
                    title: $0.title,
                    posterImageUrl: $0.posterImageUrl,
                    watchProgress: $0.watchProgress
                )
            },
            staleGridItems: Array(staleItems).map {
                MyShowsGridItem(
                    showId: $0.showId,
                    title: $0.title,
                    posterImageUrl: $0.posterImageUrl,
                    watchProgress: $0.watchProgress
                )
            },
            watchNextEpisodes: watchNextEpisodes,
            staleEpisodes: staleEpisodes,
            updatingEpisodeIds: Set(updatingEpisodeIds.map(\.int64Value))
        )
    }
}
