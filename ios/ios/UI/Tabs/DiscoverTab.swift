import Components
import DesignSystem
import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct DiscoverTab: View {
    private let presenter: DiscoverShowsPresenter
    @StateValue private var uiState: DiscoverViewState
    @StateObject private var store = SettingsAppStorage.shared
    @State private var currentIndex: Int
    @State private var toast: Toast?
    init(presenter: DiscoverShowsPresenter) {
        self.presenter = presenter
        _uiState = .init(presenter.stateValue)
        _currentIndex = State(initialValue: SettingsAppStorage.shared.savedIndex)
    }

    var body: some View {
        DiscoverScreen(
            state: uiState.toState(),
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
            onStartWatchingMoreClicked: { presenter.dispatch(action: StartWatchingMoreClicked()) },
            onNextEpisodeClicked: { episode in
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

private extension DiscoverViewState {
    func toState() -> DiscoverScreen.State {
        DiscoverScreen.State(
            title: String(\.label_discover_title),
            isLoading: isLoading,
            isEmpty: isEmpty,
            showError: showError,
            errorMessage: message?.message,
            featuredShows: featuredShowsSwift,
            nextEpisodes: nextEpisodesSwift,
            startWatchingShows: startWatchingShowsSwift,
            trendingToday: trendingTodaySwift,
            upcomingShows: upcomingShowsSwift,
            popularShows: popularShowsSwift,
            topRatedShows: topRatedShowsSwift,
            isRefreshing: isRefreshing,
            emptyContentText: String(\.generic_empty_content),
            missingApiKeyText: String(\.missing_api_key),
            retryText: String(\.button_error_retry),
            upNextTitle: String(\.label_discover_up_next),
            startWatchingTitle: startWatchingTitle,
            trendingTitle: String(\.label_discover_trending_today),
            upcomingTitle: String(\.label_discover_upcoming),
            popularTitle: String(\.label_discover_popular),
            topRatedTitle: String(\.label_discover_top_rated)
        )
    }
}
