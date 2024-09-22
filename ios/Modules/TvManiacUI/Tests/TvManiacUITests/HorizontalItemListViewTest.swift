import SnapshotTestingLib
import SwiftUI
import TvManiacUI
import XCTest

class HorizontalItemListViewTest: XCTestCase {
    func test_HorizontalItemListViewTest() {
        HorizontalItemListView(
            title: "Coming Soon",
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
            ],
            onClick: { _ in },
            onMoreClicked: {}
        )
        .background(Color.background)
        .assertSnapshot(testName: "HorizontalItemListView")
    }
}
