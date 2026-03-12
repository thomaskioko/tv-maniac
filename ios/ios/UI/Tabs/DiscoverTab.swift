import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct DiscoverTab: View {
    private let presenter: DiscoverShowsPresenter
    @StateObject @KotlinStateFlow private var uiState: DiscoverViewState
    @StateObject private var store = SettingsAppStorage.shared
    @State private var currentIndex: Int
    @State private var toast: Toast?
    @State private var selectedEpisode: SwiftNextEpisode?

    init(presenter: DiscoverShowsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.state)
        _currentIndex = State(initialValue: SettingsAppStorage.shared.savedIndex)
    }

    var body: some View {
        DiscoverScreen(
            title: String(\.label_discover_title),
            isEmpty: uiState.isEmpty,
            showError: uiState.showError,
            errorMessage: uiState.message?.message,
            featuredShows: uiState.featuredShowsSwift,
            nextEpisodes: uiState.nextEpisodesSwift,
            trendingToday: uiState.trendingTodaySwift,
            upcomingShows: uiState.upcomingShowsSwift,
            popularShows: uiState.popularShowsSwift,
            topRatedShows: uiState.topRatedShowsSwift,
            isRefreshing: uiState.isRefreshing,
            emptyContentText: String(\.generic_empty_content),
            missingApiKeyText: String(\.missing_api_key),
            retryText: String(\.button_error_retry),
            upNextTitle: String(\.label_discover_up_next),
            trendingTitle: String(\.label_discover_trending_today),
            upcomingTitle: String(\.label_discover_upcoming),
            popularTitle: String(\.label_discover_popular),
            topRatedTitle: String(\.label_discover_top_rated),
            currentIndex: $currentIndex,
            toast: $toast,
            selectedEpisode: $selectedEpisode,
            onShowClicked: { id in presenter.dispatch(action: ShowClicked(traktId: id)) },
            onSearchClicked: { presenter.dispatch(action: SearchIconClicked()) },
            onRefresh: { presenter.dispatch(action: RefreshData()) },
            onTrendingClicked: { presenter.dispatch(action: TrendingClicked()) },
            onUpcomingClicked: { presenter.dispatch(action: UpComingClicked()) },
            onPopularClicked: { presenter.dispatch(action: PopularClicked()) },
            onTopRatedClicked: { presenter.dispatch(action: TopRatedClicked()) },
            onNextEpisodeClicked: { episode in
                presenter.dispatch(action: NextEpisodeClicked(
                    showTraktId: episode.showTraktId,
                    seasonId: episode.seasonId,
                    seasonNumber: episode.seasonNumber
                ))
            },
            onNextEpisodeLongPress: { episode in
                selectedEpisode = episode
            },
            onCarouselIndexChanged: { index in
                store.savedIndex = index
            },
            episodeSheetContent: { episode in
                AnyView(episodeDetailSheet(episode: episode))
            }
        )
        .onChange(of: uiState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(type: .error, title: "Error", message: message.message)
                presenter.dispatch(action: MessageShown(id: message.id))
            }
        }
    }

    private func episodeDetailSheet(episode: SwiftNextEpisode) -> some View {
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
                    presenter.dispatch(action: MarkNextEpisodeWatched(
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
                    presenter.dispatch(action: OpenShowFromUpNext(showTraktId: episode.showTraktId))
                    selectedEpisode = nil
                }
            )
            SheetActionItem(
                icon: "list.bullet",
                label: String(\.menu_open_season),
                action: {
                    presenter.dispatch(action: OpenSeasonFromUpNext(
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
                    presenter.dispatch(action: UnfollowShowFromUpNext(showTraktId: episode.showTraktId))
                    selectedEpisode = nil
                }
            )
        }
        .presentationDetents([.large])
    }
}
