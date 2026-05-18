import DesignSystem
import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
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
