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
            title: "Trailers",
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
            openInYouTube: false,
            onMoreClicked: {}
        )
        .appPreview()
        .assertSnapshot(testName: "TrailerListView")
    }
}
