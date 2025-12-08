import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class OutlinedButtonTest: SnapshotTestCase {
    func test_OutlinedButton() {
        OutlinedButton(
            text: "Watch Trailer",
            action: {}
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "OutlinedButton")
    }

    func test_OutlinedImageButton() {
        OutlinedButton(
            text: "Watch Trailer",
            systemImageName: "film",
            action: {}
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "OutlinedImageButton")
    }
}
