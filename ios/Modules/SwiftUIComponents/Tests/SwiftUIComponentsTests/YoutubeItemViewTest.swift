import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class YoutubeItemViewTest: XCTestCase {
    func test_TrailerItemView() {
      YoutubeItemView(
            openInYouTube: false,
            key: "XZ8daibM3AE",
            name: "Series Trailer",
            thumbnailUrl: ""
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "YoutubeItemView")
    }
}
