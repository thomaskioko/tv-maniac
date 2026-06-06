import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class GridViewTest: SnapshotTestCase {
    func test_GridViewTest() {
        GridView(
            items: [
                .init(
                    showId: 1234,
                    title: "Arcane",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    showId: 123,
                    title: "The Lord of the Rings: The Rings of Power",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    showId: 12346,
                    title: "Kaos",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    showId: 124,
                    title: "Terminator",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    showId: 123_346,
                    title: "The Perfect Couple",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    showId: 2346,
                    title: "One Piece",
                    posterUrl: "",
                    inLibrary: false
                ),
            ],
            onAction: { _ in }
        )
        .appPreview()
        .assertSnapshot(testName: "GridView")
    }
}
