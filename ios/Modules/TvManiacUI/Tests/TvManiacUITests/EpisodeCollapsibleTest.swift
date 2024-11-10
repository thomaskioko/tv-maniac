import SnapshotTestingLib
import SwiftUI
import TvManiacUI
import XCTest

class EpisodeCollapsibleTest: XCTestCase {
  func test_EpisodeCollapsibleTest() {
    EpisodeCollapsible(
      episodeCount: 25,
      watchProgress: 0.6,
      isCollapsed: false,
      onCollapseClicked: {},
      onWatchedStateClicked: {}
    ) {
      VStack {}
    }
    .padding()
    .background(Color.background)
    .assertSnapshot(testName: "EpisodeCollapsible")
  }
}
