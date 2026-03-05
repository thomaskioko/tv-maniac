import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class DiscoverScreenTest: SnapshotTestCase {
    private let sampleShows: [SwiftShow] = [
        SwiftShow(traktId: 1, title: "Breaking Bad", posterUrl: nil, backdropUrl: nil, inLibrary: false, overview: "A chemistry teacher diagnosed with cancer turns to manufacturing methamphetamine."),
        SwiftShow(traktId: 2, title: "Game of Thrones", posterUrl: nil, backdropUrl: nil, inLibrary: true, overview: "Nine noble families fight for control of the lands of Westeros."),
    ]

    private let samplePosters: [SwiftShow] = [
        SwiftShow(traktId: 1, title: "Breaking Bad", posterUrl: nil, inLibrary: false),
        SwiftShow(traktId: 2, title: "Game of Thrones", posterUrl: nil, inLibrary: false),
        SwiftShow(traktId: 3, title: "The Wire", posterUrl: nil, inLibrary: false),
    ]

    func test_DiscoverScreen_Empty() {
        DiscoverScreen(
            title: "Discover",
            isEmpty: true,
            showError: false,
            errorMessage: nil,
            featuredShows: [],
            nextEpisodes: [],
            trendingToday: [],
            upcomingShows: [],
            popularShows: [],
            topRatedShows: [],
            isRefreshing: false,
            emptyContentText: "No content available",
            missingApiKeyText: "API key missing",
            retryText: "Retry",
            upNextTitle: "Up Next",
            trendingTitle: "Trending Today",
            upcomingTitle: "Upcoming",
            popularTitle: "Popular",
            topRatedTitle: "Top Rated",
            currentIndex: .constant(0),
            toast: .constant(nil),
            selectedEpisode: .constant(nil),
            onShowClicked: { _ in },
            onSearchClicked: {},
            onRefresh: {},
            onTrendingClicked: {},
            onUpcomingClicked: {},
            onPopularClicked: {},
            onTopRatedClicked: {},
            onNextEpisodeClicked: { _ in },
            onNextEpisodeLongPress: { _ in },
            onCarouselIndexChanged: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Empty")
    }

    func test_DiscoverScreen_Error() {
        DiscoverScreen(
            title: "Discover",
            isEmpty: false,
            showError: true,
            errorMessage: "Something went wrong",
            featuredShows: [],
            nextEpisodes: [],
            trendingToday: [],
            upcomingShows: [],
            popularShows: [],
            topRatedShows: [],
            isRefreshing: false,
            emptyContentText: "No content available",
            missingApiKeyText: "API key missing",
            retryText: "Retry",
            upNextTitle: "Up Next",
            trendingTitle: "Trending Today",
            upcomingTitle: "Upcoming",
            popularTitle: "Popular",
            topRatedTitle: "Top Rated",
            currentIndex: .constant(0),
            toast: .constant(nil),
            selectedEpisode: .constant(nil),
            onShowClicked: { _ in },
            onSearchClicked: {},
            onRefresh: {},
            onTrendingClicked: {},
            onUpcomingClicked: {},
            onPopularClicked: {},
            onTopRatedClicked: {},
            onNextEpisodeClicked: { _ in },
            onNextEpisodeLongPress: { _ in },
            onCarouselIndexChanged: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Error")
    }
}
