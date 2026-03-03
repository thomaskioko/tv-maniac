import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct UpNextPageContent: View {
    @Theme private var theme

    private let presenter: UpNextPresenter
    private let uiState: UpNextState
    @State private var selectedEpisode: SwiftNextEpisode?

    private var episodesSwift: [SwiftNextEpisode] {
        uiState.episodes.map { $0.toSwift() }
    }

    init(presenter: UpNextPresenter, uiState: UpNextState) {
        self.presenter = presenter
        self.uiState = uiState
    }

    var body: some View {
        contentView
            .refreshable {
                presenter.dispatch(action: RefreshUpNext())
            }
            .sheet(item: $selectedEpisode) { episode in
                EpisodeDetailSheetContent(
                    episode: EpisodeDetailInfo(
                        title: episode.showName,
                        imageUrl: episode.imageUrl,
                        episodeInfo: {
                            var text = episode.episodeNumber
                            if let runtime = episode.runtime {
                                text += " \u{2022} \(runtime)"
                            }
                            return text
                        }(),
                        overview: episode.overview.isEmpty ? nil : episode.overview,
                        rating: episode.rating,
                        voteCount: episode.voteCount
                    )
                ) {
                    SheetActionItem(
                        icon: "checkmark.circle",
                        label: String(\.menu_mark_watched),
                        action: {
                            presenter.dispatch(action: MarkWatched(
                                showTraktId: episode.showTraktId,
                                episodeId: episode.episodeId,
                                seasonNumber: episode.seasonNumber,
                                episodeNumber: episode.episodeNumberValue
                            ))
                            selectedEpisode = nil
                        }
                    )
                    SheetActionItem(
                        icon: "tv",
                        label: String(\.menu_open_show),
                        action: {
                            presenter.dispatch(action: OpenShow(showTraktId: episode.showTraktId))
                            selectedEpisode = nil
                        }
                    )
                    SheetActionItem(
                        icon: "list.bullet",
                        label: String(\.menu_open_season),
                        action: {
                            presenter.dispatch(action: OpenSeason(
                                showTraktId: episode.showTraktId,
                                seasonId: episode.seasonId,
                                seasonNumber: episode.seasonNumber
                            ))
                            selectedEpisode = nil
                        }
                    )
                    SheetActionItem(
                        icon: "minus.circle",
                        label: String(\.menu_unfollow_show),
                        action: {
                            presenter.dispatch(action: UnfollowShow(showTraktId: episode.showTraktId))
                            selectedEpisode = nil
                        }
                    )
                }
                .presentationDetents([.large])
            }
    }

    @ViewBuilder
    private var contentView: some View {
        if uiState.showLoading {
            CenteredFullScreenView {
                LoadingIndicatorView()
            }
        } else if uiState.isEmpty {
            emptyView
        } else {
            listContent
        }
    }

    private var listContent: some View {
        ScrollViewReader { proxy in
            ScrollView(showsIndicators: false) {
                LazyVStack(spacing: theme.spacing.small, pinnedViews: [.sectionHeaders]) {
                    Section {
                        ForEach(episodesSwift, id: \.episodeId) { episode in
                            UpNextListItemView(
                                episode: episode,
                                onItemClicked: { showTraktId, _ in
                                    presenter.dispatch(action: UpNextShowClicked(showTraktId: showTraktId))
                                },
                                onShowTitleClicked: { showTraktId in
                                    presenter.dispatch(action: UpNextShowClicked(showTraktId: showTraktId))
                                },
                                onMarkWatched: {
                                    presenter.dispatch(action: MarkWatched(
                                        showTraktId: episode.showTraktId,
                                        episodeId: episode.episodeId,
                                        seasonNumber: episode.seasonNumber,
                                        episodeNumber: episode.episodeNumberValue
                                    ))
                                },
                                onLongPress: {
                                    selectedEpisode = episode
                                }
                            )
                        }
                    } header: {
                        sortChipsRow
                            .background(theme.colors.background)
                    }
                }
            }
            .onChange(of: episodesSwift.first?.showTraktId) { _, _ in
                withAnimation {
                    if let firstId = episodesSwift.first?.episodeId {
                        proxy.scrollTo(firstId, anchor: .top)
                    }
                }
            }
            .onChange(of: uiState.sortOption) { _, _ in
                withAnimation {
                    if let firstId = episodesSwift.first?.episodeId {
                        proxy.scrollTo(firstId, anchor: .top)
                    }
                }
            }
        }
    }

    private var sortChipsRow: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: theme.spacing.small) {
                FilterChip(
                    label: String(\.label_upnext_sort_last_watched),
                    isSelected: uiState.sortOption == .lastWatched,
                    onTap: { presenter.dispatch(action: UpNextChangeSortOption(sortOption: .lastWatched)) }
                )
                FilterChip(
                    label: String(\.label_upnext_sort_air_date),
                    isSelected: uiState.sortOption == .airDate,
                    onTap: { presenter.dispatch(action: UpNextChangeSortOption(sortOption: .airDate)) }
                )
            }
            .padding(.horizontal)
            .padding(.vertical, theme.spacing.xSmall)
        }
    }

    private var emptyView: some View {
        VStack(spacing: 0) {
            EmptyStateView(
                title: String(\.label_upnext_empty)
            )
        }
    }
}
