import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class ShowContentItemViewTest: SnapshotTestCase {
    func test_ShowContentItemView() {
        ShowContentItemView(
            title: "The Penguin",
            imageUrl: ""
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "ShowContentItemView")
    }
}
