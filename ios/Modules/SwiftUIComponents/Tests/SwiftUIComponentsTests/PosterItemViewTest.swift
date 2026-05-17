import SnapshotTestingLib
import SwiftUI
import DesignSystem
import SwiftUIComponents
import XCTest

class PosterItemViewTest: SnapshotTestCase {
    func test_PosterItemView() {
        PosterItemView(
            title: "Arcane",
            posterUrl: ""
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "PosterItemView")
    }

    func test_PosterItemView_inLibrary() {
        PosterItemView(
            title: "Arcane",
            posterUrl: "",
            isInLibrary: true
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "PosterItemView_inLibrary")
    }
}
