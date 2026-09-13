import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import Trailers
import XCTest

class TrailersScreenTest: SnapshotTestCase {
    private let sampleTrailers: [SwiftTrailer] = [
        .init(showId: 1, key: "XZ8daibM3AE", name: "Official Trailer", youtubeThumbnailUrl: ""),
        .init(showId: 1, key: "aB9dEf3", name: "Season 2 Teaser", youtubeThumbnailUrl: ""),
        .init(showId: 1, key: "cD4eFg5", name: "Behind the Scenes", youtubeThumbnailUrl: ""),
    ]

    func test_TrailersScreen() {
        let view = TrailersScreen(
            state: TrailersScreen.State(
                title: "Trailers",
                screenState: .content(
                    selected: sampleTrailers.first,
                    trailers: sampleTrailers,
                    moreTrailersTitle: "More Trailers"
                )
            ),
            onTrailerSelected: { _ in },
            onVideoError: { _ in },
            onRetry: {},
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "TrailersScreen")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "TrailersScreen")
    }

    func test_TrailersScreen_Loading() {
        let view = TrailersScreen(
            state: TrailersScreen.State(
                title: "Trailers",
                screenState: .loading
            ),
            onTrailerSelected: { _ in },
            onVideoError: { _ in },
            onRetry: {},
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "TrailersScreen_Loading")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "TrailersScreen_Loading")
    }

    func test_TrailersScreen_Error() {
        let view = TrailersScreen(
            state: TrailersScreen.State(
                title: "Trailers",
                screenState: .error(message: "Something went wrong", retryLabel: "Retry")
            ),
            onTrailerSelected: { _ in },
            onVideoError: { _ in },
            onRetry: {},
            onBack: {}
        )
        .appPreview()
        view.assertSnapshot(layout: .defaultDevice, testName: "TrailersScreen_Error")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "TrailersScreen_Error")
    }
}
