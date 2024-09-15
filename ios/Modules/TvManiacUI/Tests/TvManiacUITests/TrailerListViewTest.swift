import SnapshotTestingLib
import SwiftUI
import TvManiacUI
import XCTest

class TrailerListViewTest: XCTestCase {
    func test_TrailerListView(){
        TrailerListView(
            trailers: [
                .init(
                    showId: 123,
                    key: "XZ8daibM3AE",
                    name: "Series Trailer",
                    youtubeThumbnailUrl: "https://i.ytimg.com/vi/XZ8daibM3AE/hqdefault.jpg"
                ),
                .init(
                    showId: 1234,
                    key: "XZ8daibM3AE",
                    name: "Series Trailer",
                    youtubeThumbnailUrl: "https://i.ytimg.com/vi/XZ8daibM3AE/hqdefault.jpg"
                ),
            ],
            openInYouTube: false
        )
        .background(Color.background)
        .assertSnapshot(testName: "TrailerListView")
    }
}
