import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class FilledImageButtonTest: SnapshotTestCase {
    func test_FilledButton() {
        FilledImageButton(
            text: "Add To Library",
            action: {}
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "FilledButton")
    }

    func test_FilledImageButton() {
        FilledImageButton(
            text: "Watch Trailer",
            systemImageName: "film",
            action: {}
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "FilledImageButton")
    }
}
