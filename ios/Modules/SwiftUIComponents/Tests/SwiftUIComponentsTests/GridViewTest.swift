import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class GridViewTest: SnapshotTestCase {
    func test_GridViewTest() {
        GridView(
            items: [
                .init(
                    traktId: 1234,
                    title: "Arcane",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    traktId: 123,
                    title: "The Lord of the Rings: The Rings of Power",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    traktId: 12346,
                    title: "Kaos",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    traktId: 124,
                    title: "Terminator",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    traktId: 123_346,
                    title: "The Perfect Couple",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    traktId: 2346,
                    title: "One Piece",
                    posterUrl: "",
                    inLibrary: false
                ),
            ],
            onAction: { _ in }
        )
        .themedPreview()
        .assertSnapshot(testName: "GridView")
    }
}
