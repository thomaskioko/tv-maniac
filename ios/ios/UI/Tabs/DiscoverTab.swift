import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct DiscoverTab: View {
    private let presenter: DiscoverShowsPresenter
    @StateObject @KotlinStateFlow private var uiState: DiscoverViewState
    @StateObject private var store = SettingsAppStorage.shared
    @State private var currentIndex: Int
    @State private var toast: Toast?
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
            selectedEpisode: .constant(nil),
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
                presenter.dispatch(action: DiscoverEpisodeLongPressed(
                    showTraktId: episode.showTraktId,
                    episodeId: episode.episodeId
                ))
            },
            onCarouselIndexChanged: { index in
                store.savedIndex = index
            },
            episodeSheetContent: { _ in AnyView(EmptyView()) }
        )
        .onChange(of: uiState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(type: .error, title: "Error", message: message.message)
                presenter.dispatch(action: MessageShown(id: message.id))
            }
        }
    }
}
