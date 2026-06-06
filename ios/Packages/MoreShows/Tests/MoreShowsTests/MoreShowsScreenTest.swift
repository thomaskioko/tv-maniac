import Components
import DesignSystem
import Models
import MoreShows
import SnapshotTestingLib
import SwiftUI
import XCTest

class MoreShowsScreenTest: SnapshotTestCase {
    private let sampleItems: [ShowPosterImage] = [
        .init(showId: 1, title: "Arcane", posterUrl: nil),
        .init(showId: 2, title: "Loki", posterUrl: nil),
        .init(showId: 3, title: "The Bear", posterUrl: nil),
        .init(showId: 4, title: "Severance", posterUrl: nil),
        .init(showId: 5, title: "Shogun", posterUrl: nil),
        .init(showId: 6, title: "Fallout", posterUrl: nil),
    ]

    func test_MoreShowsScreen() {
        MoreShowsScreen(
            state: MoreShowsScreen.State(
                title: "Popular",
                items: sampleItems,
                isLoadingMore: false,
                hasNextPage: false,
                loadError: nil,
                retryLabel: "Retry"
            ),
            toast: .constant(nil),
            onItemAppear: { _ in },
            onLoadMore: {},
            onAction: { _ in },
            onBack: {},
            onRetry: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MoreShowsScreen")
    }

    func test_MoreShowsScreen_LoadingMore() {
        MoreShowsScreen(
            state: MoreShowsScreen.State(
                title: "Trending",
                items: sampleItems,
                isLoadingMore: true,
                hasNextPage: true,
                loadError: nil,
                retryLabel: "Retry"
            ),
            toast: .constant(nil),
            onItemAppear: { _ in },
            onLoadMore: {},
            onAction: { _ in },
            onBack: {},
            onRetry: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MoreShowsScreen_LoadingMore")
    }

    func test_MoreShowsScreen_Error() {
        MoreShowsScreen(
            state: MoreShowsScreen.State(
                title: "Top Rated",
                items: sampleItems,
                isLoadingMore: false,
                hasNextPage: false,
                loadError: "Failed to load more shows. Please try again.",
                retryLabel: "Retry"
            ),
            toast: .constant(nil),
            onItemAppear: { _ in },
            onLoadMore: {},
            onAction: { _ in },
            onBack: {},
            onRetry: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MoreShowsScreen_Error")
    }

    func test_MoreShowsScreen_Empty() {
        MoreShowsScreen(
            state: MoreShowsScreen.State(
                title: "Upcoming",
                items: [],
                isLoadingMore: false,
                hasNextPage: false,
                loadError: nil,
                retryLabel: "Retry"
            ),
            toast: .constant(nil),
            onItemAppear: { _ in },
            onLoadMore: {},
            onAction: { _ in },
            onBack: {},
            onRetry: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MoreShowsScreen_Empty")
    }
}
