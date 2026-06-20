import Components
import DesignSystem
import Models
import ShowDetails
import SnapshotTestingLib
import SwiftUI
import XCTest

class TrailerListViewTest: SnapshotTestCase {
    func test_TrailerListView() {
        TrailerListView(
            trailers: [
                .init(
                    showId: 123,
                    key: "XZ8daibM3AE",
                    name: "Series Trailer",
                    youtubeThumbnailUrl: ""
                ),
                .init(
                    showId: 1234,
                    key: "XZ8daibM3AE",
                    name: "Series Trailer",
                    youtubeThumbnailUrl: ""
                ),
            ],
            openInYouTube: false
        )
        .appPreview()
        .assertSnapshot(testName: "TrailerListView")
    }
}
