import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class OutlinedButtonTest: SnapshotTestCase {
    func test_OutlinedButton() {
        OutlinedButton(
            text: "Watch Trailer",
            action: {}
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "OutlinedButton")
    }

    func test_OutlinedImageButton() {
        OutlinedButton(
            text: "Watch Trailer",
            systemImageName: "film",
            action: {}
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "OutlinedImageButton")
    }
}
