import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class BottomTabItemTest: XCTestCase {
    func test_bottomTabItem_selected() {
        BottomTabItem(
            title: "Discover",
            systemImage: "tv",
            isActive: true,
            action: {}
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "BottomTabItem_Selected")
    }

    func test_bottomTabItem_unselected() {
        BottomTabItem(
            title: "Discover",
            systemImage: "tv",
            isActive: false,
            action: {}
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "BottomTabItem_Unselected")
    }
}
