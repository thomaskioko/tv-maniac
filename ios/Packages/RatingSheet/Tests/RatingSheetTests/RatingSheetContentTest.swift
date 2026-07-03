import DesignSystem
import RatingSheet
import SnapshotTestingLib
import SwiftUI
import XCTest

class RatingSheetContentTest: SnapshotTestCase {
    func test_RatingSheetContent_Unrated() {
        makeSheet(userRating: nil)
            .assertSnapshot(layout: .defaultDevice, testName: "RatingSheetContent_Unrated")
    }

    func test_RatingSheetContent_Rated() {
        makeSheet(userRating: 8)
            .assertSnapshot(layout: .defaultDevice, testName: "RatingSheetContent_Rated")
    }

    func test_RatingSheetContent_HalfRated() {
        makeSheet(userRating: 7)
            .assertSnapshot(layout: .defaultDevice, testName: "RatingSheetContent_HalfRated")
    }

    private func makeSheet(userRating: Int?) -> some View {
        RatingSheetContent(
            title: "Your rating",
            removeLabel: "Remove rating",
            userRating: userRating,
            onRatingSelected: { _ in },
            onRemove: {}
        )
        .appPreview()
    }
}
