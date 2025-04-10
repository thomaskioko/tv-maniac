import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class CircularButtonTest: XCTestCase {
    func _test_CircularButton() {
        CircularButton(iconName: "arrow.backward", action: {})
            .padding()
            .background(Color.background)
            .assertSnapshot(testName: "CircularButton")
    }
}
