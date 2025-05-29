import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class HorizontalShowContentViewViewTest: XCTestCase {
    func test_HorizontalShowContentView() {
        HorizontalShowContentView(
            title: "Trending Today",
            items: [
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
            onClick: { _ in },
            onMoreClicked: {}
        )
            .padding()
            .background(Color.background)
            .assertSnapshot(testName: "HorizontalShowContentView")
    }
}
