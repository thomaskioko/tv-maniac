import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class CastCardViewTest: SnapshotTestCase {
    func test_CastCardViewWithImage() {
        CastCardView(
            profileUrl: "",
            name: "Rosario Dawson",
            characterName: "Claire Temple"
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "CastCardView")
    }
}
