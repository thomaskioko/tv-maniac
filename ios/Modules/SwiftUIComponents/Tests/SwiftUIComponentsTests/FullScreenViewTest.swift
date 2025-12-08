import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class FullScreenViewTest: SnapshotTestCase {
    func test_FullScreenView() {
        FullScreenView(
            systemName: "exclamationmark.triangle.fill",
            message: "Something went wrong",
            buttonText: "Retry"
        )
        .padding()
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "FullScreenView")
    }
}
