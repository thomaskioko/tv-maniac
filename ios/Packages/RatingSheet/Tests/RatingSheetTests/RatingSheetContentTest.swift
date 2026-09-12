import DesignSystem
import RatingSheet
import SnapshotTestingLib
import SwiftUI
import XCTest

class RatingSheetContentTest: SnapshotTestCase {
    func test_RatingSheetContent_Unrated() {
        let view = buildRatingSheet(title: "Lioness", subtitle: "2023", posterUrl: "/lioness.jpg", userRating: nil)
        view.assertSnapshot(layout: .defaultDevice, testName: "RatingSheetContent_Unrated")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "RatingSheetContent_Unrated")
    }

    func test_RatingSheetContent_Rated() {
        let view = buildRatingSheet(
            title: "Sacrificial Soldiers",
            subtitle: "Lioness • S1E1",
            backdropUrl: "/sacrificial-soldiers.jpg",
            userRating: 8
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "RatingSheetContent_Rated")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "RatingSheetContent_Rated")
    }

    func test_RatingSheetContent_SeasonRated() {
        let view = buildRatingSheet(title: "Season 1", subtitle: "Lioness", posterUrl: "/season-1.jpg", userRating: 7)
        view.assertSnapshot(layout: .defaultDevice, testName: "RatingSheetContent_SeasonRated")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "RatingSheetContent_SeasonRated")
    }

    private func buildRatingSheet(
        title: String,
        subtitle: String?,
        posterUrl: String? = nil,
        backdropUrl: String? = nil,
        userRating: Int?
    ) -> some View {
        RatingSheetContent(
            headerLabel: "You're rating",
            title: title,
            subtitle: subtitle,
            posterUrl: posterUrl,
            backdropUrl: backdropUrl,
            scoreLabel: "Your rating",
            removeLabel: "Remove rating",
            userRating: userRating,
            onRatingSelected: { _ in },
            onRemove: {}
        )
        .appPreview()
    }
}
