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
                    showId: 123,
                    showName: "The Walking Dead: Daryl Dixon",
                    showPoster: "/poster1.jpg",
                    episodeId: 456,
                    episodeTitle: "L'âme Perdue",
                    episodeNumber: "S02E01",
                    runtime: "45 min",
                    stillImage: "/still1.jpg",
                    overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                    isNew: true
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
                    showId: 123,
                    showName: "The Walking Dead: Daryl Dixon",
                    showPoster: "/poster1.jpg",
                    episodeId: 456,
                    episodeTitle: "L'âme Perdue",
                    episodeNumber: "S02E01",
                    runtime: "45 min",
                    stillImage: "/still1.jpg",
                    overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                    isNew: true
                ),
                SwiftNextEpisode(
                    showId: 124,
                    showName: "Wednesday",
                    showPoster: "/poster2.jpg",
                    episodeId: 789,
                    episodeTitle: "Wednesday's Child Is Full of Woe",
                    episodeNumber: "S02E02",
                    runtime: "50 min",
                    stillImage: "/still2.jpg",
                    overview: "Wednesday arrives at Nevermore Academy and immediately gets off on the wrong foot.",
                    isNew: false
                ),
                SwiftNextEpisode(
                    showId: 125,
                    showName: "House of the Dragon",
                    showPoster: "/poster3.jpg",
                    episodeId: 790,
                    episodeTitle: "The Heirs of the Dragon",
                    episodeNumber: "S03E01",
                    runtime: "66 min",
                    stillImage: "/still3.jpg",
                    overview: "King Viserys hosts a tournament to celebrate the birth of his second child.",
                    isNew: true
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
                    showId: 123,
                    showName: "The Walking Dead: Daryl Dixon",
                    showPoster: "/poster1.jpg",
                    episodeId: 456,
                    episodeTitle: "L'âme Perdue",
                    episodeNumber: "S02E01",
                    runtime: "45 min",
                    stillImage: "/still1.jpg",
                    overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                    isNew: true
                ),
                SwiftNextEpisode(
                    showId: 124,
                    showName: "Wednesday",
                    showPoster: "/poster2.jpg",
                    episodeId: 789,
                    episodeTitle: "Wednesday's Child Is Full of Woe",
                    episodeNumber: "S02E02",
                    runtime: "50 min",
                    stillImage: "/still2.jpg",
                    overview: "Wednesday arrives at Nevermore Academy and immediately gets off on the wrong foot.",
                    isNew: false
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
                    showId: 123,
                    showName: "The Walking Dead: Daryl Dixon",
                    showPoster: "/poster1.jpg",
                    episodeId: 456,
                    episodeTitle: "L'âme Perdue",
                    episodeNumber: "S02E01",
                    runtime: "45 min",
                    stillImage: "/still1.jpg",
                    overview: "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                    isNew: true
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
