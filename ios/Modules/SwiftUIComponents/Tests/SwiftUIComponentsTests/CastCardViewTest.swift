import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class CastCardViewTest: SnapshotTestCase {
    func test_CastCardViewWithImage() {
        CastCardView(
            profileUrl: "https://image.tmdb.org/t/p/w780/1mm7JGHIUX3GRRGXEV9QCzsI0ao.jpg",
            name: "Rosario Dawson",
            characterName: "Claire Temple"
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "CastCardView")
    }
}
