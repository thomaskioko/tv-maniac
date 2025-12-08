import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class YoutubeItemViewTest: SnapshotTestCase {
    func test_TrailerItemView() {
        YoutubeItemView(
            openInYouTube: false,
            key: "XZ8daibM3AE",
            name: "Series Trailer",
            thumbnailUrl: ""
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "YoutubeItemView")
    }
}
