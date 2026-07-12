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
            trendingVisible: state.trendingVisible,
            upcomingVisible: state.upcomingVisible,
            popularVisible: state.popularVisible,
            topRatedVisible: state.topRatedVisible,
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
    let trendingVisible: Bool
    let upcomingVisible: Bool
    let popularVisible: Bool
    let topRatedVisible: Bool
    let onShowClicked: (Int64) -> Void
    let onTrendingMoreClicked: () -> Void
    let onUpcomingMoreClicked: () -> Void
    let onPopularMoreClicked: () -> Void
    let onTopRatedMoreClicked: () -> Void

    init(
        trendingTitle: String,
        upcomingTitle: String,
        popularTitle: String,
        topRatedTitle: String,
        trendingShows: [SwiftShow],
        upcomingShows: [SwiftShow],
        popularShows: [SwiftShow],
        topRatedShows: [SwiftShow],
        trendingVisible: Bool = true,
        upcomingVisible: Bool = true,
        popularVisible: Bool = true,
        topRatedVisible: Bool = true,
        onShowClicked: @escaping (Int64) -> Void,
        onTrendingMoreClicked: @escaping () -> Void,
        onUpcomingMoreClicked: @escaping () -> Void,
        onPopularMoreClicked: @escaping () -> Void,
        onTopRatedMoreClicked: @escaping () -> Void
    ) {
        self.trendingTitle = trendingTitle
        self.upcomingTitle = upcomingTitle
        self.popularTitle = popularTitle
        self.topRatedTitle = topRatedTitle
        self.trendingShows = trendingShows
        self.upcomingShows = upcomingShows
        self.popularShows = popularShows
        self.topRatedShows = topRatedShows
        self.trendingVisible = trendingVisible
        self.upcomingVisible = upcomingVisible
        self.popularVisible = popularVisible
        self.topRatedVisible = topRatedVisible
        self.onShowClicked = onShowClicked
        self.onTrendingMoreClicked = onTrendingMoreClicked
        self.onUpcomingMoreClicked = onUpcomingMoreClicked
        self.onPopularMoreClicked = onPopularMoreClicked
        self.onTopRatedMoreClicked = onTopRatedMoreClicked
    }

    var body: some View {
        VStack {
            if trendingVisible {
                HorizontalShowContentView(
                    title: trendingTitle,
                    chevronStyle: .chevronOnly,
                    cardStyle: .poster,
                    items: trendingShows,
                    onClick: onShowClicked,
                    onMoreClicked: onTrendingMoreClicked
                )
            }

            if upcomingVisible {
                HorizontalShowContentView(
                    title: upcomingTitle,
                    chevronStyle: .chevronOnly,
                    cardStyle: .poster,
                    items: upcomingShows,
                    onClick: onShowClicked,
                    onMoreClicked: onUpcomingMoreClicked
                )
            }

            if popularVisible {
                HorizontalShowContentView(
                    title: popularTitle,
                    chevronStyle: .chevronOnly,
                    cardStyle: .poster,
                    items: popularShows,
                    onClick: onShowClicked,
                    onMoreClicked: onPopularMoreClicked
                )
            }

            if topRatedVisible {
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
