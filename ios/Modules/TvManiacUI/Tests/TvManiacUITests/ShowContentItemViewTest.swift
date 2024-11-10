import SnapshotTestingLib
import SwiftUI
import TvManiacUI
import XCTest

class ShowContentItemViewTest: XCTestCase {
  func test_ShowContentItemView() {
    ShowContentItemView(
      title: "The Penguin",
      imageUrl: ""
    )
    .padding()
    .background(Color.background)
    .assertSnapshot(testName: "ShowContentItemView")
  }
}
