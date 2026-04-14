import SwiftUI

struct DiscoverListContent: View {
    @Theme private var appTheme

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
    let onShowClicked: (Int64) -> Void
    let onTrendingClicked: () -> Void
    let onUpcomingClicked: () -> Void
    let onPopularClicked: () -> Void
    let onTopRatedClicked: () -> Void
    let onNextEpisodeClicked: (SwiftNextEpisode) -> Void

    var body: some View {
        VStack {
            NextEpisodesSection(
                title: upNextTitle,
                episodes: nextEpisodes,
                chevronStyle: .chevronOnly,
                onEpisodeClick: onNextEpisodeClicked
            )

            HorizontalItemListView(
                title: trendingTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: trendingToday,
                onClick: onShowClicked,
                onMoreClicked: onTrendingClicked
            )

            HorizontalItemListView(
                title: upcomingTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: upcomingShows,
                onClick: onShowClicked,
                onMoreClicked: onUpcomingClicked
            )

            HorizontalItemListView(
                title: popularTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: popularShows,
                onClick: onShowClicked,
                onMoreClicked: onPopularClicked
            )

            HorizontalItemListView(
                title: topRatedTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: topRatedShows,
                onClick: onShowClicked,
                onMoreClicked: onTopRatedClicked
            )
        }
        .padding(.top, appTheme.spacing.medium)
        .background(appTheme.colors.background)
        .offset(y: -10)
    }
}
