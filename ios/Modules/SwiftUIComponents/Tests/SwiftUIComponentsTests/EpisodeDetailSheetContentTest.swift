import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class EpisodeDetailSheetContentTest: SnapshotTestCase {

    func test_EpisodeDetailSheetContent_AllActions() {
        makeSheet(
            title: "The Walking Dead: Daryl Dixon",
            episodeInfo: "S02E01 \u{2022} The Walking Dead",
            overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
            rating: 8.5,
            voteCount: 1234,
            showAllActions: true,
            isWatched: false
        )
        .assertSnapshot(layout: .defaultDevice, testName: "EpisodeDetailSheetContent_AllActions")
    }

    func test_EpisodeDetailSheetContent_Watched() {
        makeSheet(
            title: "Wednesday",
            episodeInfo: "S02E03 \u{2022} Wednesday",
            overview: "Wednesday arrives at Nevermore Academy and begins investigating a series of mysterious events.",
            rating: 7.9,
            voteCount: 856,
            showAllActions: true,
            isWatched: true
        )
        .assertSnapshot(layout: .defaultDevice, testName: "EpisodeDetailSheetContent_Watched")
    }

    func test_EpisodeDetailSheetContent_SeasonDetailsSource() {
        makeSheet(
            title: "House of the Dragon",
            episodeInfo: "S03E01 \u{2022} House of the Dragon",
            overview: "King Viserys hosts a tournament to celebrate the birth of his heir.",
            rating: nil,
            voteCount: nil,
            showAllActions: false,
            isWatched: false
        )
        .assertSnapshot(layout: .defaultDevice, testName: "EpisodeDetailSheetContent_SeasonDetailsSource")
    }

    func test_EpisodeDetailSheetContent_NoOverview() {
        makeSheet(
            title: "Severance",
            episodeInfo: "S02E05 \u{2022} Severance",
            overview: nil,
            rating: 9.1,
            voteCount: 2500,
            showAllActions: true,
            isWatched: false
        )
        .assertSnapshot(layout: .defaultDevice, testName: "EpisodeDetailSheetContent_NoOverview")
    }

    private func makeSheet(
        title: String,
        episodeInfo: String,
        overview: String?,
        rating: Double?,
        voteCount: Int64?,
        showAllActions: Bool,
        isWatched: Bool
    ) -> some View {
        EpisodeDetailSheetContent(
            episode: EpisodeDetailInfo(
                title: title,
                imageUrl: nil,
                episodeInfo: episodeInfo,
                overview: overview,
                rating: rating,
                voteCount: voteCount
            )
        ) {
            SheetActionItem(
                icon: isWatched ? "checkmark.circle.fill" : "checkmark.circle",
                label: isWatched ? "Mark unwatched" : "Mark watched",
                action: {}
            )
            if showAllActions {
                SheetActionItem(icon: "tv", label: "Open show", action: {})
                SheetActionItem(icon: "list.bullet", label: "Open season", action: {})
                SheetActionItem(icon: "minus.circle", label: "Unfollow show", action: {})
            }
        }
        .themedPreview()
    }
}
