import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class SeasonChipViewListTest: SnapshotTestCase {
    func test_SeasonChipViewList() {
        SeasonChipViewList(
            items: [
                .init(tvShowId: 23, seasonId: 23, seasonNumber: 1, name: "Season 1"),
                .init(tvShowId: 123, seasonId: 123, seasonNumber: 2, name: "Season 2"),
            ],
            onClick: { _ in }
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "SeasonChipViewList")
    }
}
