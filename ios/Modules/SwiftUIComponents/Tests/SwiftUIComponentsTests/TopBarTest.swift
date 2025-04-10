import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class TopBarTest: XCTestCase {
    func _test_TopBar() {
        TopBar(
            progress: 0,
            title: "Movie Title",
            isRefreshing: true,
            onBackClicked: {},
            onRefreshClicked: {}
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "TopBar")
    }
}
