import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class TopBarTest: SnapshotTestCase {
    func _test_TopBar() {
        TopBar(
            progress: 0,
            title: "Movie Title",
            isRefreshing: true,
            onBackClicked: {},
            onRefreshClicked: {}
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "TopBar")
    }
}
