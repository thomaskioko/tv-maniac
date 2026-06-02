import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class ChipViewTest: SnapshotTestCase {
    func test_ChipView() {
        ChipView(label: "Drama")
            .padding()
            .appPreview()
            .assertSnapshot(testName: "ChipView")
    }
}
