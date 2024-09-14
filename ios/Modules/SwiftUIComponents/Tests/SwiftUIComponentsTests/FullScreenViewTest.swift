import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class FullScreenViewTest: XCTestCase {
    func test_FullScreenView() {
        FullScreenView(
            systemName: "exclamationmark.triangle.fill",
            message: "Something went wrong",
            buttonText: "Retry"
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(layout: .defaultDevice, testName: "FullScreenView")
    }
}
