import SnapshotTestingLib
import SwiftUI
import TvManiacKit
import XCTest

class TrailerListViewTest: XCTestCase {
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
    .background(Color.background)
    .assertSnapshot(testName: "TrailerListView")
  }
}
