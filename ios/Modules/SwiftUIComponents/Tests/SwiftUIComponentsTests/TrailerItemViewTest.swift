import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class TrailerItemViewTest: XCTestCase {
    func test_TrailerItemView() {
        TrailerItemView(
            openInYouTube: false,
            key: "XZ8daibM3AE",
            name: "Series Trailer",
            thumbnailUrl: ""
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "TrailerItemView")
    }
}
