import Components
import DesignSystem
import Models
import SwiftUI
import TvManiacKit

struct DiscoverCatalogSection: View {
    private let presenter: DiscoverCatalogPresenter
    @StateValue private var state: DiscoverCatalogState

    init(presenter: DiscoverCatalogPresenter) {
        self.presenter = presenter
        _state = .init(presenter.stateValue)
    }

    var body: some View {
        DiscoverCatalogContent(
            trendingTitle: state.trendingTitle,
            upcomingTitle: state.upcomingTitle,
            popularTitle: state.popularTitle,
            topRatedTitle: state.topRatedTitle,
            trendingShows: state.trendingShowsSwift,
            upcomingShows: state.upcomingShowsSwift,
            popularShows: state.popularShowsSwift,
            topRatedShows: state.topRatedShowsSwift,
            onShowClicked: { id in
                presenter.dispatch(action: CatalogShowClicked(showId: id))
            },
            onTrendingMoreClicked: {
                presenter.dispatch(action: TrendingMoreClicked())
            },
            onUpcomingMoreClicked: {
                presenter.dispatch(action: UpcomingMoreClicked())
            },
            onPopularMoreClicked: {
                presenter.dispatch(action: PopularMoreClicked())
            },
            onTopRatedMoreClicked: {
                presenter.dispatch(action: TopRatedMoreClicked())
            }
        )
    }
}

struct DiscoverCatalogContent: View {
    let trendingTitle: String
    let upcomingTitle: String
    let popularTitle: String
    let topRatedTitle: String
    let trendingShows: [SwiftShow]
    let upcomingShows: [SwiftShow]
    let popularShows: [SwiftShow]
    let topRatedShows: [SwiftShow]
    let onShowClicked: (Int64) -> Void
    let onTrendingMoreClicked: () -> Void
    let onUpcomingMoreClicked: () -> Void
    let onPopularMoreClicked: () -> Void
    let onTopRatedMoreClicked: () -> Void

    var body: some View {
        VStack {
            HorizontalShowContentView(
                title: trendingTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: trendingShows,
                onClick: onShowClicked,
                onMoreClicked: onTrendingMoreClicked
            )

            HorizontalShowContentView(
                title: upcomingTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: upcomingShows,
                onClick: onShowClicked,
                onMoreClicked: onUpcomingMoreClicked
            )

            HorizontalShowContentView(
                title: popularTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: popularShows,
                onClick: onShowClicked,
                onMoreClicked: onPopularMoreClicked
            )

            HorizontalShowContentView(
                title: topRatedTitle,
                chevronStyle: .chevronOnly,
                cardStyle: .poster,
                items: topRatedShows,
                onClick: onShowClicked,
                onMoreClicked: onTopRatedMoreClicked
            )
        }
    }
}

#Preview("Catalog - Empty") {
    DiscoverCatalogContent(
        trendingTitle: "Trending Today",
        upcomingTitle: "Upcoming",
        popularTitle: "Popular",
        topRatedTitle: "Top Rated",
        trendingShows: [],
        upcomingShows: [],
        popularShows: [],
        topRatedShows: [],
        onShowClicked: { _ in },
        onTrendingMoreClicked: {},
        onUpcomingMoreClicked: {},
        onPopularMoreClicked: {},
        onTopRatedMoreClicked: {}
    )
    .appPreview()
}

#Preview("Catalog - Content") {
    let samplePosters: [SwiftShow] = [
        SwiftShow(showId: 1, title: "Breaking Bad", posterUrl: nil, inLibrary: false),
        SwiftShow(showId: 2, title: "Game of Thrones", posterUrl: nil, inLibrary: false),
        SwiftShow(showId: 3, title: "The Wire", posterUrl: nil, inLibrary: false),
    ]
    DiscoverCatalogContent(
        trendingTitle: "Trending Today",
        upcomingTitle: "Upcoming",
        popularTitle: "Popular",
        topRatedTitle: "Top Rated",
        trendingShows: samplePosters,
        upcomingShows: samplePosters,
        popularShows: samplePosters,
        topRatedShows: samplePosters,
        onShowClicked: { _ in },
        onTrendingMoreClicked: {},
        onUpcomingMoreClicked: {},
        onPopularMoreClicked: {},
        onTopRatedMoreClicked: {}
    )
    .appPreview()
}
