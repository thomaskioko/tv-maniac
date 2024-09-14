import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ChipViewTest: XCTestCase {
    func test_ChipView() {
        ChipView(label: "Drama")
            .padding()
            .background(Color.background)
            .assertSnapshot(testName: "ChipView")
    }
}
