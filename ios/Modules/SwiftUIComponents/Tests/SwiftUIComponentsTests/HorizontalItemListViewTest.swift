import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class HorizontalItemListViewTest: SnapshotTestCase {
    func test_HorizontalItemListViewTest() {
        HorizontalItemListView(
            title: "Coming Soon",
            items: [
                .init(
                    traktId: 1234,
                    title: "Arcane",
                    posterUrl: "",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    traktId: 123,
                    title: "The Lord of the Rings: The Rings of Power",
                    posterUrl: "",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    traktId: 12346,
                    title: "Kaos",
                    posterUrl: "",
                    backdropUrl: nil,
                    inLibrary: false
                ),
            ],
            onClick: { _ in },
            onMoreClicked: {}
        )
        .themedPreview()
        .assertSnapshot(testName: "HorizontalItemListView")
    }
}
