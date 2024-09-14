import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class OutlinedButtonTest: XCTestCase {
    func test_OutlinedButton() {
        OutlinedButton(
            text: "Watch Trailer",
            action: {}
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "OutlinedButton")
    }

    func test_OutlinedImageButton() {
        OutlinedButton(
            text: "Watch Trailer",
            systemImageName: "film",
            action: {}
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "OutlinedImageButton")
    }
}
