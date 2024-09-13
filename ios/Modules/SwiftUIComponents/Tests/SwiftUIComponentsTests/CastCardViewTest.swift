import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class CastCardViewTest: XCTestCase {
    func test_CastCardViewWithImage() {
        CastCardView(
            profileUrl: "https://image.tmdb.org/t/p/w780/1mm7JGHIUX3GRRGXEV9QCzsI0ao.jpg",
            name: "Rosario Dawson",
            characterName: "Claire Temple"
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "CastCardView")
    }
}
