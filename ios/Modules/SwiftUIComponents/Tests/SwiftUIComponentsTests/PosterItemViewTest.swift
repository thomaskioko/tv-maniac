import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class PosterItemViewTest: XCTestCase {
    func test_PosterItemView() {
        PosterItemView(
            title: "Arcane",
            posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg"
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "PosterItemView")
    }

    func test_PosterItemView_inLibrary() {
        PosterItemView(
            title: "Arcane",
            posterUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
            isInLibrary: true
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "PosterItemView_inLibrary")
    }
}
