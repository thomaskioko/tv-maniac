import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class BorderTextViewTest: SnapshotTestCase {
    func test_BorderTextView() {
        BorderTextView(text: "2024")
            .padding()
            .appPreview()
            .assertSnapshot(testName: "BorderTextView")
    }

    func test_BorderTextView_Tinted() {
        BorderTextView(
            text: "Continuing",
            colorOpacity: 0.12,
            borderOpacity: 0.12,
            weight: .bold
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "BorderTextView_Tinted")
    }
}
