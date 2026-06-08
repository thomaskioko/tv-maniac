import Components
import DesignSystem
import Models
import SeasonDetails
import SnapshotTestingLib
import SwiftUI
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
        .appPreview()
        .assertSnapshot(testName: "EpisodeCollapsible")
    }
}
