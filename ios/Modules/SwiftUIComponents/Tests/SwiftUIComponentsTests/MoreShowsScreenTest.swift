import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class MoreShowsScreenTest: SnapshotTestCase {
    private let sampleItems: [ShowPosterImage] = [
        .init(traktId: 1, title: "Arcane", posterUrl: nil),
        .init(traktId: 2, title: "Loki", posterUrl: nil),
        .init(traktId: 3, title: "The Bear", posterUrl: nil),
        .init(traktId: 4, title: "Severance", posterUrl: nil),
        .init(traktId: 5, title: "Shogun", posterUrl: nil),
        .init(traktId: 6, title: "Fallout", posterUrl: nil),
    ]

    func test_MoreShowsScreen() {
        MoreShowsScreen(
            title: "Popular",
            items: sampleItems,
            isLoadingMore: false,
            hasNextPage: false,
            loadError: nil,
            toast: .constant(nil),
            onItemAppear: { _ in },
            onLoadMore: {},
            onAction: { _ in },
            onBack: {},
            onRetry: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MoreShowsScreen")
    }

    func test_MoreShowsScreen_LoadingMore() {
        MoreShowsScreen(
            title: "Trending",
            items: sampleItems,
            isLoadingMore: true,
            hasNextPage: true,
            loadError: nil,
            toast: .constant(nil),
            onItemAppear: { _ in },
            onLoadMore: {},
            onAction: { _ in },
            onBack: {},
            onRetry: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MoreShowsScreen_LoadingMore")
    }

    func test_MoreShowsScreen_Error() {
        MoreShowsScreen(
            title: "Top Rated",
            items: sampleItems,
            isLoadingMore: false,
            hasNextPage: false,
            loadError: "Failed to load more shows. Please try again.",
            toast: .constant(nil),
            onItemAppear: { _ in },
            onLoadMore: {},
            onAction: { _ in },
            onBack: {},
            onRetry: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MoreShowsScreen_Error")
    }

    func test_MoreShowsScreen_Empty() {
        MoreShowsScreen(
            title: "Upcoming",
            items: [],
            isLoadingMore: false,
            hasNextPage: false,
            loadError: nil,
            toast: .constant(nil),
            onItemAppear: { _ in },
            onLoadMore: {},
            onAction: { _ in },
            onBack: {},
            onRetry: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MoreShowsScreen_Empty")
    }
}
