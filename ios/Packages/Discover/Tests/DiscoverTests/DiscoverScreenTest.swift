import Components
import DesignSystem
@testable import Discover
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class DiscoverScreenTest: SnapshotTestCase {
    private let sampleShows: [SwiftShow] = [
        SwiftShow(
            showId: 1,
            title: "Breaking Bad",
            posterUrl: nil,
            backdropUrl: nil,
            inLibrary: false,
            overview: "A chemistry teacher diagnosed with cancer turns to manufacturing methamphetamine."
        ),
        SwiftShow(
            showId: 2,
            title: "Game of Thrones",
            posterUrl: nil,
            backdropUrl: nil,
            inLibrary: true,
            overview: "Nine noble families fight for control of the lands of Westeros."
        ),
    ]

    private let samplePosters: [SwiftShow] = [
        SwiftShow(showId: 1, title: "Breaking Bad", posterUrl: nil, inLibrary: false),
        SwiftShow(showId: 2, title: "Game of Thrones", posterUrl: nil, inLibrary: false),
        SwiftShow(showId: 3, title: "The Wire", posterUrl: nil, inLibrary: false),
    ]

    private let sampleEpisodes: [SwiftNextEpisode] = [
        SwiftNextEpisode(
            showId: 1,
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
            showId: 2,
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
        let view = LoadingIndicatorView()
            .appScreen()
            .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Loading")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "DiscoverScreen_Loading")
    }

    func test_DiscoverScreen_Empty() {
        let view = EmptyStateView(
            systemName: "list.bullet.below.rectangle",
            title: "No content available",
            message: "API key missing",
            buttonText: "Retry",
            action: {}
        )
        .appScreen()
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Empty")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "DiscoverScreen_Empty")
    }

    func test_DiscoverScreen_Error() {
        let view = EmptyStateView(
            systemName: "exclamationmark.arrow.triangle.2.circlepath",
            title: "Something went wrong",
            buttonText: "Retry",
            action: {}
        )
        .appScreen()
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Error")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "DiscoverScreen_Error")
    }

    func test_DiscoverUpNextSection_Content() {
        let view = DiscoverUpNextContent(
            title: "Up Next",
            episodes: sampleEpisodes,
            onEpisodeClicked: { _ in }
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_UpNext")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "DiscoverScreen_UpNext")
    }

    func test_DiscoverStartWatchingSection_Content() {
        let view = DiscoverStartWatchingContent(
            title: "Start Watching",
            shows: samplePosters,
            onShowClicked: { _ in },
            onMoreClicked: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_StartWatching")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "DiscoverScreen_StartWatching")
    }

    func test_DiscoverCatalogSection_Content() {
        let view = DiscoverCatalogContent(
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
        view.assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Catalog")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "DiscoverScreen_Catalog")
    }

    func test_DiscoverCatalogSection_HiddenSection() {
        let view = DiscoverCatalogContent(
            trendingTitle: "Trending Today",
            upcomingTitle: "Upcoming",
            popularTitle: "Popular",
            topRatedTitle: "Top Rated",
            trendingShows: samplePosters,
            upcomingShows: samplePosters,
            popularShows: samplePosters,
            topRatedShows: samplePosters,
            upcomingVisible: false,
            onShowClicked: { _ in },
            onTrendingMoreClicked: {},
            onUpcomingMoreClicked: {},
            onPopularMoreClicked: {},
            onTopRatedMoreClicked: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Catalog_HiddenSection")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "DiscoverScreen_Catalog_HiddenSection")
    }

    func test_DiscoverUpNextSection_DynamicTypeXXXLarge() {
        let view = DiscoverUpNextContent(
            title: "Up Next",
            episodes: sampleEpisodes,
            onEpisodeClicked: { _ in }
        )
        .appPreview()
        .environment(\.dynamicTypeSize, .xxxLarge)
        view.assertSnapshot(layout: .defaultDevice, styles: .dark, testName: "DiscoverScreen_UpNext_DynamicTypeXXXLarge")
        view.assertSnapshot(
            layout: .defaultDevice,
            styles: .dark,
            liquidGlass: true,
            testName: "DiscoverScreen_UpNext_DynamicTypeXXXLarge"
        )
    }

    func test_DiscoverUpNextSection_DynamicTypeAX3() {
        let view = DiscoverUpNextContent(
            title: "Up Next",
            episodes: sampleEpisodes,
            onEpisodeClicked: { _ in }
        )
        .appPreview()
        .environment(\.dynamicTypeSize, .accessibility3)
        view.assertSnapshot(layout: .defaultDevice, styles: .dark, testName: "DiscoverScreen_UpNext_DynamicTypeAX3")
        view.assertSnapshot(
            layout: .defaultDevice,
            styles: .dark,
            liquidGlass: true,
            testName: "DiscoverScreen_UpNext_DynamicTypeAX3"
        )
    }

    func test_DiscoverFeaturedSection_Empty() {
        let view = DiscoverFeaturedContent(
            shows: [],
            currentIndex: .constant(0),
            selectedShow: .constant(nil),
            isDraggingCarousel: .constant(false),
            onShowClicked: { _ in },
            onIndexChanged: { _ in }
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Featured_Empty")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "DiscoverScreen_Featured_Empty")
    }

    func test_DiscoverFeaturedSection_Content() {
        let view = DiscoverFeaturedContent(
            shows: sampleShows,
            currentIndex: .constant(0),
            selectedShow: .constant(sampleShows.first),
            isDraggingCarousel: .constant(false),
            onShowClicked: { _ in },
            onIndexChanged: { _ in }
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "DiscoverScreen_Featured_Content")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "DiscoverScreen_Featured_Content")
    }
}
