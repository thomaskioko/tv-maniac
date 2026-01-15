import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class HorizontalShowContentViewViewTest: SnapshotTestCase {
    func test_HorizontalShowContentView() {
        HorizontalShowContentView(
            title: "Trending Today",
            items: [
                .init(
                    traktId: 124,
                    title: "Terminator",
                    posterUrl: "",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    traktId: 123_346,
                    title: "The Perfect Couple",
                    posterUrl: "",
                    backdropUrl: nil,
                    inLibrary: false
                ),
                .init(
                    traktId: 2346,
                    title: "One Piece",
                    posterUrl: "",
                    backdropUrl: nil,
                    inLibrary: false
                ),
            ],
            onClick: { _ in },
            onMoreClicked: {}
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "HorizontalShowContentView")
    }
}
