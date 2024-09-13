import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class BorderTextViewTest: XCTestCase {
    func test_BorderTextView() {
        BorderTextView(text: "2024")
            .padding()
            .background(Color.background)
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
        .background(Color.background)
        .assertSnapshot(testName: "BorderTextView_Tinted")
    }
}
