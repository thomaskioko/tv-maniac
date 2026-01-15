import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class TrailerListViewTest: SnapshotTestCase {
    func test_TrailerListView() {
        TrailerListView(
            trailers: [
                .init(
                    showTmdbId: 123,
                    key: "XZ8daibM3AE",
                    name: "Series Trailer",
                    youtubeThumbnailUrl: ""
                ),
                .init(
                    showTmdbId: 1234,
                    key: "XZ8daibM3AE",
                    name: "Series Trailer",
                    youtubeThumbnailUrl: ""
                ),
            ],
            openInYouTube: false
        )
        .themedPreview()
        .assertSnapshot(testName: "TrailerListView")
    }
}
