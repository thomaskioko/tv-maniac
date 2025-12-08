import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class NavigationTopBarTest: SnapshotTestCase {
    func _test_NavigationTopBarTest() {
        NavigationTopBar(
            topBarTitle: "Upcoming",
            imageName: "arrow.backward",
            onBackClicked: {}
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "NavigationTopBar")
    }
}
