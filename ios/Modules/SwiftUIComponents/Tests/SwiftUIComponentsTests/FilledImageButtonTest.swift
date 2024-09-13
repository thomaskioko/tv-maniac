import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class FilledImageButtonTest: XCTestCase {
    func test_FilledButton() {
        FilledImageButton(
            text: "Add To Library",
            action: {}
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "FilledButton")
    }

    func test_FilledImageButton() {
        FilledImageButton(
            text: "Watch Trailer",
            systemImageName: "film",
            action: {}
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "FilledImageButton")
    }
}
