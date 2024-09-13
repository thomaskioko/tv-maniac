import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class BottomNavigationTest: XCTestCase {
    func test_bottomSheetNavigation() {
        BottomNavigation(
            actions: [
                BottomTabAction(
                    title: "Discover",
                    systemImage: "tv",
                    isActive: true,
                    action: {}
                ),
                BottomTabAction(
                    title: "Search",
                    systemImage: "magnifyingglass",
                    isActive: false,
                    action: {}
                ),
                BottomTabAction(
                    title: "Library",
                    systemImage: "list.bullet.below.rectangle",
                    isActive: false,
                    action: {}
                ),
                BottomTabAction(
                    title: "Settings",
                    systemImage: "gearshape",
                    isActive: false,
                    action: {}
                )
            ]
        )
        .background(Color.background)
        .assertSnapshot(testName: "BottomNavigation")
    }
}
