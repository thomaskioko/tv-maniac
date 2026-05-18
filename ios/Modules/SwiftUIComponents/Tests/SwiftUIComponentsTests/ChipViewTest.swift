import DesignSystem
import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ChipViewTest: SnapshotTestCase {
    func test_ChipView() {
        ChipView(label: "Drama")
            .padding()
            .appPreview()
            .assertSnapshot(testName: "ChipView")
    }
}
