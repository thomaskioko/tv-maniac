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

    private let sampleEpisodes: [SwiftNextEpisode] = [
        SwiftNextEpisode(
            showTraktId: 1,
            showName: "Breaking Bad",
            imageUrl: nil,
            episodeId: 101,
            episodeTitle: "Pilot",
            episodeNumber: "S01E01",
            runtime: "58 min",
            overview: "A high school chemistry teacher turns to manufacturing methamphetamine.",
            badge: .premiere,
            remainingEpisodes: 7,
            watchedCount: 1,
            totalCount: 8
        ),
        SwiftNextEpisode(
            showTraktId: 2,
            showName: "Game of Thrones",
            imageUrl: nil,
            episodeId: 201,
            episodeTitle: "Winter Is Coming",
            episodeNumber: "S01E01",
            runtime: "62 min",
            overview: "Eddard Stark is torn between his family and an old friend.",
            badge: .new,
            remainingEpisodes: 9,
            watchedCount: 0,
            totalCount: 10
        ),
    ]

    func test_DiscoverScreen_Loading() {
        DiscoverScreen(
            title: "Discover",
            isLoading: true,
            isEmpty: false,
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
            onCarouselIndexChanged: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Loading")
    }

    func test_DiscoverScreen_Empty() {
        DiscoverScreen(
            title: "Discover",
            isLoading: false,
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
            onCarouselIndexChanged: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Empty")
    }

    func test_DiscoverScreen_Error() {
        DiscoverScreen(
            title: "Discover",
            isLoading: false,
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
            onCarouselIndexChanged: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Error")
    }

    func test_DiscoverScreen_Loaded() {
        DiscoverScreen(
            title: "Discover",
            isLoading: false,
            isEmpty: false,
            showError: false,
            errorMessage: nil,
            featuredShows: sampleShows,
            nextEpisodes: sampleEpisodes,
            trendingToday: samplePosters,
            upcomingShows: samplePosters,
            popularShows: samplePosters,
            topRatedShows: samplePosters,
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
            onCarouselIndexChanged: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Loaded")
    }
}
