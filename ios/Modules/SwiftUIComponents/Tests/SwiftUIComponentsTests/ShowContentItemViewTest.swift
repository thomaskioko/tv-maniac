import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
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
