import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class EmptyStateViewTest: SnapshotTestCase {
    func test_EmptyStateView() {
        EmptyStateView(
            systemName: "exclamationmark.triangle",
            title: "Something went wrong",
            buttonText: "Retry"
        )
        .padding()
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "EmptyStateView")
    }

    func test_EmptyStateView_WithMessage() {
        EmptyStateView(
            title: "Nothing here yet",
            message: "Shows you follow will appear here."
        )
        .padding()
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "EmptyStateView_WithMessage")
    }
}
