import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct UpNextPageContent: View {
    @Theme private var theme

    private let presenter: UpNextPresenter
    private let uiState: UpNextState
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
                presenter.dispatch(action_______: RefreshUpNext())
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
                                    presenter.dispatch(action_______: UpNextShowClicked(showTraktId: showTraktId))
                                },
                                onShowTitleClicked: { showTraktId in
                                    presenter.dispatch(action_______: UpNextShowClicked(showTraktId: showTraktId))
                                },
                                onMarkWatched: {
                                    presenter.dispatch(action_______: MarkWatched(
                                        showTraktId: episode.showTraktId,
                                        episodeId: episode.episodeId,
                                        seasonNumber: episode.seasonNumber,
                                        episodeNumber: episode.episodeNumberValue
                                    ))
                                },
                                onLongPress: {
                                    presenter.dispatch(action_______: UpNextEpisodeLongPressed(episodeId: episode.episodeId))
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
                    onTap: { presenter.dispatch(action_______: UpNextChangeSortOption(sortOption: .lastWatched)) }
                )
                FilterChip(
                    label: String(\.label_upnext_sort_air_date),
                    isSelected: uiState.sortOption == .airDate,
                    onTap: { presenter.dispatch(action_______: UpNextChangeSortOption(sortOption: .airDate)) }
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
