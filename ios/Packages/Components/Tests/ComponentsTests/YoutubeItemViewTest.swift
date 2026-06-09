import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class YoutubeItemViewTest: SnapshotTestCase {
    func test_TrailerItemView() throws {
        try XCTSkipIf(
            ProcessInfo.processInfo.environment["CI"] != nil,
            "Skipping on CI - YouTubePlayer requires network access"
        )

        YoutubeItemView(
            openInYouTube: false,
            key: "XZ8daibM3AE",
            name: "Series Trailer",
            thumbnailUrl: ""
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "YoutubeItemView")
    }
}
