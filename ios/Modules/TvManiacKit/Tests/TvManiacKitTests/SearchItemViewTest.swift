import SnapshotTestingLib
import SwiftUI
import TvManiacKit
import XCTest

class SearchItemViewTest: XCTestCase {
  func test_SearchItemViewTest() {
    SearchItemView(
      title: "Arcane",
      overview: "In 1997, a haunted scientist brushes his family aside for an all-consuming project. In 2022, a renegade fighter battles a powerful robot for vital data.",
      imageUrl: "",
      status: "Ended",
      year: "2024",
      voteAverage: 5.4
    )
    .padding()
    .background(Color.background)
    .assertSnapshot(testName: "SearchItemView")
  }
}
