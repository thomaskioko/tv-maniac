import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class CastCardViewTest: SnapshotTestCase {
    func test_CastCardViewWithImage() {
        CastCardView(
            profileUrl: "",
            name: "Rosario Dawson",
            characterName: "Claire Temple"
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "CastCardView")
    }
}
