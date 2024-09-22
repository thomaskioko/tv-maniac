import SnapshotTestingLib
import SwiftUI
import TvManiacUI
import XCTest


class GridViewTest: XCTestCase {
    func test_HeaderView() {
        GridView(
            items: [
                .init(
                    tmdbId: 1234,
                    title: "Arcane",
                    posterUrl: "",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 123,
                    title: "The Lord of the Rings: The Rings of Power",
                    posterUrl: "",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 12346,
                    title: "Kaos",
                    posterUrl: "",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 124,
                    title: "Terminator",
                    posterUrl: "",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 123346,
                    title: "The Perfect Couple",
                    posterUrl: "",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    tmdbId: 2346,
                    title: "One Piece",
                    posterUrl: "",
                    backdropUrl: nil,
                    inLibrary: false
                ),
            ],
            onAction: { _ in }
        )
        .background(Color.background)
        .assertSnapshot(testName: "GridView")
    }
}
