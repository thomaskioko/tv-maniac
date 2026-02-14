import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class NextEpisodesSectionTest: SnapshotTestCase {
    func test_NextEpisodesSectionEmpty() {
        NextEpisodesSection(
            title: "Up Next",
            episodes: [],
            markWatchedLabel: "Mark as Watched",
            unfollowShowLabel: "Unfollow Show",
            openSeasonLabel: "Open Season",
            onEpisodeClick: { _, _ in }
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "NextEpisodesSectionEmpty")
    }

    func test_NextEpisodesSectionSingleEpisode() {
        NextEpisodesSection(
            title: "Up Next",
            episodes: [
                SwiftNextEpisode(
                    showTraktId: 123,
                    showName: "The Walking Dead: Daryl Dixon",
                    imageUrl: "/still1.jpg",
                    episodeId: 456,
                    episodeTitle: "L'âme Perdue",
                    episodeNumber: "S02E01",
                    runtime: "45 min",
                    overview: "Daryl washes ashore in France and struggles to piece together how he got there and why."
                ),
            ],
            markWatchedLabel: "Mark as Watched",
            unfollowShowLabel: "Unfollow Show",
            openSeasonLabel: "Open Season",
            onEpisodeClick: { _, _ in }
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "NextEpisodesSectionSingleEpisode")
    }

    func test_NextEpisodesSectionMultipleEpisodes() {
        NextEpisodesSection(
            title: "Up Next",
            episodes: [
                SwiftNextEpisode(
                    showTraktId: 123,
                    showName: "The Walking Dead: Daryl Dixon",
                    imageUrl: "/still1.jpg",
                    episodeId: 456,
                    episodeTitle: "L'âme Perdue",
                    episodeNumber: "S02E01",
                    runtime: "45 min",
                    overview: "Daryl washes ashore in France and struggles to piece together how he got there and why."
                ),
                SwiftNextEpisode(
                    showTraktId: 124,
                    showName: "Wednesday",
                    imageUrl: "/still2.jpg",
                    episodeId: 789,
                    episodeTitle: "Wednesday's Child Is Full of Woe",
                    episodeNumber: "S02E02",
                    runtime: "50 min",
                    overview: "Wednesday arrives at Nevermore Academy and immediately gets off on the wrong foot."
                ),
                SwiftNextEpisode(
                    showTraktId: 125,
                    showName: "House of the Dragon",
                    imageUrl: "/still3.jpg",
                    episodeId: 790,
                    episodeTitle: "The Heirs of the Dragon",
                    episodeNumber: "S03E01",
                    runtime: "66 min",
                    overview: "King Viserys hosts a tournament to celebrate the birth of his second child."
                ),
            ],
            markWatchedLabel: "Mark as Watched",
            unfollowShowLabel: "Unfollow Show",
            openSeasonLabel: "Open Season",
            onEpisodeClick: { _, _ in }
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "NextEpisodesSectionMultipleEpisodes")
    }

    func test_NextEpisodesSectionWithChevronOnly() {
        NextEpisodesSection(
            title: "Continue Watching",
            episodes: [
                SwiftNextEpisode(
                    showTraktId: 123,
                    showName: "The Walking Dead: Daryl Dixon",
                    imageUrl: "/still1.jpg",
                    episodeId: 456,
                    episodeTitle: "L'âme Perdue",
                    episodeNumber: "S02E01",
                    runtime: "45 min",
                    overview: "Daryl washes ashore in France and struggles to piece together how he got there and why."
                ),
                SwiftNextEpisode(
                    showTraktId: 124,
                    showName: "Wednesday",
                    imageUrl: "/still2.jpg",
                    episodeId: 789,
                    episodeTitle: "Wednesday's Child Is Full of Woe",
                    episodeNumber: "S02E02",
                    runtime: "50 min",
                    overview: "Wednesday arrives at Nevermore Academy and immediately gets off on the wrong foot."
                ),
            ],
            chevronStyle: .chevronOnly,
            markWatchedLabel: "Mark as Watched",
            unfollowShowLabel: "Unfollow Show",
            openSeasonLabel: "Open Season",
            onEpisodeClick: { _, _ in }
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "NextEpisodesSectionWithChevronOnly")
    }

    func test_NextEpisodesSectionWithChevronTitle() {
        NextEpisodesSection(
            title: "Continue Watching",
            episodes: [
                SwiftNextEpisode(
                    showTraktId: 123,
                    showName: "The Walking Dead: Daryl Dixon",
                    imageUrl: nil,
                    episodeId: 456,
                    episodeTitle: "L'âme Perdue",
                    episodeNumber: "S02E01",
                    runtime: "45 min",
                    overview: "Daryl washes ashore in France and struggles to piece together how he got there and why."
                ),
            ],
            chevronStyle: .withTitle("See All"),
            markWatchedLabel: "Mark as Watched",
            unfollowShowLabel: "Unfollow Show",
            openSeasonLabel: "Open Season",
            onEpisodeClick: { _, _ in }
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "NextEpisodesSectionWithChevronTitle")
    }
}
