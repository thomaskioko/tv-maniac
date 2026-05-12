import SwiftUI

struct DiscoverListContent: View {
    struct State {
        let upNextTitle: String
        let trendingTitle: String
        let upcomingTitle: String
        let popularTitle: String
        let topRatedTitle: String
        let nextEpisodes: [SwiftNextEpisode]
        let trendingToday: [SwiftShow]
        let upcomingShows: [SwiftShow]
        let popularShows: [SwiftShow]
        let topRatedShows: [SwiftShow]
    }

    @Theme private var appTheme

    let state: State
    let onShowClicked: (Int64) -> Void
    let onTrendingClicked: () -> Void
    let onUpcomingClicked: () -> Void
    let onPopularClicked: () -> Void
    let onTopRatedClicked: () -> Void
    let onNextEpisodeClicked: (SwiftNextEpisode) -> Void

    var body: some View {
        VStack {
            NextEpisodesSection(
                title: state.upNextTitle,
                episodes: state.nextEpisodes,
                chevronStyle: .chevronOnly,
                onEpisodeClick: onNextEpisodeClicked
            )

            HorizontalItemListView(
                title: state.trendingTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: state.trendingToday,
                onClick: onShowClicked,
                onMoreClicked: onTrendingClicked
            )

            HorizontalItemListView(
                title: state.upcomingTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: state.upcomingShows,
                onClick: onShowClicked,
                onMoreClicked: onUpcomingClicked
            )

            HorizontalItemListView(
                title: state.popularTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: state.popularShows,
                onClick: onShowClicked,
                onMoreClicked: onPopularClicked
            )

            HorizontalItemListView(
                title: state.topRatedTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: state.topRatedShows,
                onClick: onShowClicked,
                onMoreClicked: onTopRatedClicked
            )
        }
        .padding(.top, appTheme.spacing.medium)
        .background(appTheme.colors.background)
        .offset(y: -10)
    }
}
