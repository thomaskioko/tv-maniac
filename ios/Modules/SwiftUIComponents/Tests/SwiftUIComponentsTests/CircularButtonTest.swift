import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class CircularButtonTest: SnapshotTestCase {
    func _test_CircularButton() {
        CircularButton(iconName: "arrow.backward", action: {})
            .padding()
            .themedPreview()
            .assertSnapshot(testName: "CircularButton")
    }
}
