import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class EpisodeCollapsibleTest: SnapshotTestCase {
    func test_EpisodeCollapsibleTest() {
        EpisodeCollapsible(
            title: "Episodes",
            episodeCount: 25,
            watchProgress: 0.6,
            isCollapsed: false,
            onCollapseClicked: {},
            onWatchedStateClicked: {}
        ) {
            VStack {}
        }
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "EpisodeCollapsible")
    }
}
