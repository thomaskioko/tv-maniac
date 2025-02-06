import SnapshotTestingLib
import SwiftUI
import TvManiacKit
import XCTest


class GridViewTest: XCTestCase {
    func test_GridViewTest() {
        GridView(
            items: [
                .init(
                    tmdbId: 1234,
                    title: "Arcane",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    tmdbId: 123,
                    title: "The Lord of the Rings: The Rings of Power",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    tmdbId: 12346,
                    title: "Kaos",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    tmdbId: 124,
                    title: "Terminator",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    tmdbId: 123346,
                    title: "The Perfect Couple",
                    posterUrl: "",
                    inLibrary: false
                ),
                .init(
                    tmdbId: 2346,
                    title: "One Piece",
                    posterUrl: "",
                    inLibrary: false
                ),
            ],
            onAction: { _ in }
        )
        .background(Color.background)
        .assertSnapshot(testName: "GridView")
    }
}
