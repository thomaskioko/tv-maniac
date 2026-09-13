import Components
import DesignSystem
import EpisodeDetail
import SnapshotTestingLib
import SwiftUI
import XCTest

class EpisodeDetailSheetContentTest: SnapshotTestCase {
    func test_EpisodeDetailSheetContent_AllActions() {
        let view = makeSheet(
            episode: EpisodeDetailSheetInfo(
                title: "The Walking Dead: Daryl Dixon",
                imageUrl: nil,
                episodeInfo: "S02E01 \u{2022} The Walking Dead",
                overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                rating: 8.5,
                voteCount: 1234
            ),
            showAllActions: true,
            isWatched: false
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "EpisodeDetailSheetContent_AllActions")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "EpisodeDetailSheetContent_AllActions")
    }

    func test_EpisodeDetailSheetContent_Watched() {
        let view = makeSheet(
            episode: EpisodeDetailSheetInfo(
                title: "Wednesday",
                imageUrl: nil,
                episodeInfo: "S02E03 \u{2022} Wednesday",
                overview: "Wednesday arrives at Nevermore Academy and begins investigating a series of mysterious events.",
                rating: 7.9,
                voteCount: 856
            ),
            showAllActions: true,
            isWatched: true
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "EpisodeDetailSheetContent_Watched")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "EpisodeDetailSheetContent_Watched")
    }

    func test_EpisodeDetailSheetContent_SeasonDetailsSource() {
        let view = makeSheet(
            episode: EpisodeDetailSheetInfo(
                title: "House of the Dragon",
                imageUrl: nil,
                episodeInfo: "S03E01 \u{2022} House of the Dragon",
                overview: "King Viserys hosts a tournament to celebrate the birth of his heir.",
                rating: nil,
                voteCount: nil
            ),
            showAllActions: false,
            isWatched: false
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "EpisodeDetailSheetContent_SeasonDetailsSource")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "EpisodeDetailSheetContent_SeasonDetailsSource")
    }

    func test_EpisodeDetailSheetContent_NoOverview() {
        let view = makeSheet(
            episode: EpisodeDetailSheetInfo(
                title: "Severance",
                imageUrl: nil,
                episodeInfo: "S02E05 \u{2022} Severance",
                overview: nil,
                rating: 9.1,
                voteCount: 2500
            ),
            showAllActions: true,
            isWatched: false
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "EpisodeDetailSheetContent_NoOverview")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "EpisodeDetailSheetContent_NoOverview")
    }

    func test_EpisodeDetailSheetContent_WatchedAgain() {
        let view = makeSheet(
            episode: EpisodeDetailSheetInfo(
                title: "The Walking Dead: Daryl Dixon",
                imageUrl: nil,
                episodeInfo: "S02E01 \u{2022} The Walking Dead",
                overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                rating: 8.5,
                voteCount: 1234,
                isWatched: true,
                playCount: 3
            ),
            showAllActions: true,
            isWatched: true
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "EpisodeDetailSheetContent_WatchedAgain")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "EpisodeDetailSheetContent_WatchedAgain")
    }

    func test_EpisodeDetailSheetContent_SeenOnce() {
        let view = makeSheet(
            episode: EpisodeDetailSheetInfo(
                title: "The Walking Dead: Daryl Dixon",
                imageUrl: nil,
                episodeInfo: "S02E01 \u{2022} The Walking Dead",
                overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                rating: 8.5,
                voteCount: 1234,
                isWatched: true,
                playCount: 1
            ),
            showAllActions: false,
            isWatched: true
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "EpisodeDetailSheetContent_SeenOnce")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "EpisodeDetailSheetContent_SeenOnce")
    }

    private func makeSheet(
        episode: EpisodeDetailSheetInfo,
        showAllActions: Bool,
        isWatched: Bool
    ) -> some View {
        EpisodeDetailSheetContent(episode: episode) {
            SheetActionItem(icon: "star", label: "Rate episode", action: {})
            SheetActionItem(
                icon: isWatched ? "arrow.counterclockwise" : "checkmark.circle",
                label: isWatched ? "Watch again" : "Mark watched",
                action: {}
            )
            if isWatched {
                SheetActionItem(icon: "checkmark.circle.badge.xmark", label: "Mark unwatched", action: {})
            }
            if showAllActions {
                SheetActionItem(icon: "tv", label: "Open show", action: {})
                SheetActionItem(icon: "list.bullet", label: "Open season", action: {})
                SheetActionItem(icon: "minus.circle", label: "Unfollow show", action: {})
            }
        }
        .appPreview()
    }
}
