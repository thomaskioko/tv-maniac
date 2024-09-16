import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class NavigationTopBarTest: XCTestCase {
    func test_NavigationTopBarTest() {
        NavigationTopBar(
            topBarTitle: "Upcoming",
            imageName: "arrow.backward",
            onBackClicked: {}
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "NavigationTopBar")
    }
}