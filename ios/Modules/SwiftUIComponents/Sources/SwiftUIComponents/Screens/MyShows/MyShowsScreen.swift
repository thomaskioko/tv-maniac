import Components
import DesignSystem
import Models
import SwiftUI

public struct MyShowsScreen: View {
    public struct State {
        public let emptyText: String
        public let upToDateText: String
        public let upNextSectionTitle: String
        public let staleSectionTitle: String
        public let premiereLabel: String
        public let newLabel: String
        public let isLoading: Bool
        public let isGridMode: Bool
        public let query: String
        public let watchNextGridItems: [MyShowsGridItem]
        public let staleGridItems: [MyShowsGridItem]
        public let watchNextEpisodes: [SwiftNextEpisode]
        public let staleEpisodes: [SwiftNextEpisode]
        public let updatingEpisodeIds: Set<Int64>

        public init(
            emptyText: String,
            upToDateText: String,
            upNextSectionTitle: String,
            staleSectionTitle: String,
            premiereLabel: String,
            newLabel: String,
            isLoading: Bool,
            isGridMode: Bool,
            query: String,
            watchNextGridItems: [MyShowsGridItem],
            staleGridItems: [MyShowsGridItem],
            watchNextEpisodes: [SwiftNextEpisode],
            staleEpisodes: [SwiftNextEpisode],
            updatingEpisodeIds: Set<Int64> = []
        ) {
            self.emptyText = emptyText
            self.upToDateText = upToDateText
            self.upNextSectionTitle = upNextSectionTitle
            self.staleSectionTitle = staleSectionTitle
            self.premiereLabel = premiereLabel
            self.newLabel = newLabel
            self.isLoading = isLoading
            self.isGridMode = isGridMode
            self.query = query
            self.watchNextGridItems = watchNextGridItems
            self.staleGridItems = staleGridItems
            self.watchNextEpisodes = watchNextEpisodes
            self.staleEpisodes = staleEpisodes
            self.updatingEpisodeIds = updatingEpisodeIds
        }
    }

    @Environment(\.appTheme) private var appTheme
    @Environment(\.widthSizeClass) private var widthSizeClass

    private let state: State
    private let onShowClicked: (Int64) -> Void
    private let onEpisodeClicked: (Int64, Int64) -> Void
    private let onShowTitleClicked: (Int64) -> Void
    private let onMarkWatched: (SwiftNextEpisode) -> Void
    private let onRefresh: () -> Void

    @Namespace private var animation

    public init(
        state: State,
        onShowClicked: @escaping (Int64) -> Void,
        onEpisodeClicked: @escaping (Int64, Int64) -> Void,
        onShowTitleClicked: @escaping (Int64) -> Void,
        onMarkWatched: @escaping (SwiftNextEpisode) -> Void,
        onRefresh: @escaping () -> Void
    ) {
        self.state = state
        self.onShowClicked = onShowClicked
        self.onEpisodeClicked = onEpisodeClicked
        self.onShowTitleClicked = onShowTitleClicked
        self.onMarkWatched = onMarkWatched
        self.onRefresh = onRefresh
    }

    public var body: some View {
        contentView
            .refreshable { onRefresh() }
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
                                onMarkWatched: { onMarkWatched(episode) },
                                isUpdating: state.updatingEpisodeIds.contains(episode.episodeId)
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
                                onMarkWatched: { onMarkWatched(episode) },
                                isUpdating: state.updatingEpisodeIds.contains(episode.episodeId)
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

    private func gridItemsView(items: [MyShowsGridItem]) -> some View {
        LazyVGrid(
            columns: ImageDimens.posterGridColumns(widthSizeClass, spacing: ImageDimens.gridItemSpacing),
            spacing: ImageDimens.gridItemSpacing
        ) {
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
