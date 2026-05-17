import SnapshotTestingLib
import SwiftUI
import DesignSystem
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
        .appPreview()
        .assertSnapshot(testName: "NavigationTopBar")
    }
}
