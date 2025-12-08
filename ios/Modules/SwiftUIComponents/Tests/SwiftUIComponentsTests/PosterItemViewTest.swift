import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class PosterItemViewTest: SnapshotTestCase {
    func test_PosterItemView() {
        PosterItemView(
            title: "Arcane",
            posterUrl: ""
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "PosterItemView")
    }

    func test_PosterItemView_inLibrary() {
        PosterItemView(
            title: "Arcane",
            posterUrl: "",
            isInLibrary: true
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "PosterItemView_inLibrary")
    }
}
