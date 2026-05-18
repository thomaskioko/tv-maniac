import DesignSystem
import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class HorizontalShowContentViewTest: SnapshotTestCase {
    private let sampleItems: [SwiftShow] = [
        .init(traktId: 124, title: "Terminator", posterUrl: "", backdropUrl: nil, inLibrary: false),
        .init(traktId: 123_346, title: "The Perfect Couple", posterUrl: "", backdropUrl: nil, inLibrary: false),
        .init(traktId: 2346, title: "One Piece", posterUrl: "", backdropUrl: nil, inLibrary: false),
    ]

    func test_HorizontalShowContentView_Metallic() {
        HorizontalShowContentView(
            title: "Trending Today",
            cardStyle: .metallic,
            items: sampleItems,
            onClick: { _ in }
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "HorizontalShowContentView_Metallic")
    }

    func test_HorizontalShowContentView_Poster() {
        HorizontalShowContentView(
            title: "Trending Today",
            chevronStyle: .chevronOnly,
            cardStyle: .poster,
            items: sampleItems,
            onClick: { _ in }
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "HorizontalShowContentView_Poster")
    }

    func test_HorizontalShowContentView_Backdrop() {
        HorizontalShowContentView(
            title: "Coming Soon",
            cardStyle: .backdrop,
            items: sampleItems,
            onClick: { _ in }
        )
        .padding()
        .appPreview()
        .assertSnapshot(testName: "HorizontalShowContentView_Backdrop")
    }
}
