import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class FilledVerticalIconButtonTest: SnapshotTestCase {
    func test_FilledVerticalIconButton() {
        FilledVerticalIconButton(
            text: "Track",
            systemImage: "plus.circle.fill",
            action: {}
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "FilledVerticalIconButton")
    }

    func test_FilledVerticalIconButton_CustomContainerColor() {
        FilledVerticalIconButton(
            text: "Stop Tracking",
            systemImage: "minus.circle.fill",
            containerColor: .red.opacity(0.65),
            action: {}
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "FilledVerticalIconButton_CustomContainerColor")
    }
}
