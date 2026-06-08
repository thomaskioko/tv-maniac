import Components
import DesignSystem
import Models
import SwiftUI
import UpNext

struct DiscoverListContent: View {
    struct State {
        let upNextTitle: String
        let startWatchingTitle: String
        let trendingTitle: String
        let upcomingTitle: String
        let popularTitle: String
        let topRatedTitle: String
        let nextEpisodes: [SwiftNextEpisode]
        let startWatchingShows: [SwiftShow]
        let trendingToday: [SwiftShow]
        let upcomingShows: [SwiftShow]
        let popularShows: [SwiftShow]
        let topRatedShows: [SwiftShow]
    }

    @Environment(\.appTheme) private var appTheme

    let state: State
    let onShowClicked: (Int64) -> Void
    let onStartWatchingMoreClicked: () -> Void
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

            HorizontalShowContentView(
                title: state.startWatchingTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: state.startWatchingShows,
                onClick: onShowClicked,
                onMoreClicked: onStartWatchingMoreClicked
            )

            HorizontalShowContentView(
                title: state.trendingTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: state.trendingToday,
                onClick: onShowClicked,
                onMoreClicked: onTrendingClicked
            )

            HorizontalShowContentView(
                title: state.upcomingTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: state.upcomingShows,
                onClick: onShowClicked,
                onMoreClicked: onUpcomingClicked
            )

            HorizontalShowContentView(
                title: state.popularTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: state.popularShows,
                onClick: onShowClicked,
                onMoreClicked: onPopularClicked
            )

            HorizontalShowContentView(
                title: state.topRatedTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: state.topRatedShows,
                onClick: onShowClicked,
                onMoreClicked: onTopRatedClicked
            )
        }
        .padding(.top, appTheme.spacing.medium)
        .background(.appBackground)
        .offset(y: -10)
    }
}
