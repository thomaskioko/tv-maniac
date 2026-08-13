import Components
import DesignSystem
import Models
import MyShows
import SnapshotTestingLib
import SwiftUI
import XCTest

class MyShowsScreenTest: SnapshotTestCase {
    private let sampleGridItems: [MyShowsGridItem] = [
        MyShowsGridItem(showId: 1, title: "Breaking Bad", posterImageUrl: nil, watchProgress: 0.7),
        MyShowsGridItem(showId: 2, title: "Game of Thrones", posterImageUrl: nil, watchProgress: 0.3),
    ]

    private let sampleEpisodes: [SwiftNextEpisode] = [
        SwiftNextEpisode(
            showId: 1,
            showName: "The Walking Dead: Daryl Dixon",
            imageUrl: nil,
            episodeId: 123,
            episodeTitle: "L'ame Perdue",
            episodeNumber: "S02 | E01",
            runtime: "45 min",
            overview: "Daryl washes ashore in France.",
            watchedCount: 3,
            totalCount: 8
        ),
        SwiftNextEpisode(
            showId: 2,
            showName: "Severance",
            imageUrl: nil,
            episodeId: 456,
            episodeTitle: "Woe's Hollow",
            episodeNumber: "S02 | E05",
            runtime: "52 min",
            overview: "The severed team goes on a retreat.",
            watchedCount: 5,
            totalCount: 10
        ),
    ]

    private func makeState(
        emptyText: String = "No content",
        isLoading: Bool = false,
        layout: SwiftListStyle = .grid,
        watchNextGridItems: [MyShowsGridItem] = [],
        staleGridItems: [MyShowsGridItem] = [],
        watchNextEpisodes: [SwiftNextEpisode] = []
    ) -> MyShowsScreen.State {
        MyShowsScreen.State(
            emptyText: emptyText,
            upToDateText: "Up to date",
            upNextSectionTitle: "Up Next",
            staleSectionTitle: "Not watched for a while",
            premiereLabel: "Premiere",
            newLabel: "New",
            isLoading: isLoading,
            layout: layout,
            query: "",
            watchNextGridItems: watchNextGridItems,
            staleGridItems: staleGridItems,
            watchNextEpisodes: watchNextEpisodes,
            staleEpisodes: []
        )
    }

    private func screen(state: MyShowsScreen.State) -> some View {
        MyShowsScreen(
            state: state,
            onShowClicked: { _ in },
            onEpisodeClicked: { _, _ in },
            onShowTitleClicked: { _ in },
            onMarkWatched: { _ in },
            onRefresh: {}
        )
    }

    func test_MyShowsScreen_Loading() {
        screen(state: makeState(isLoading: true))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_Loading")
    }

    func test_MyShowsScreen_GridMode() {
        screen(state: makeState(watchNextGridItems: sampleGridItems))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_GridMode")
    }

    func test_MyShowsScreen_GridMode_WithStale() {
        let staleItems: [MyShowsGridItem] = [
            MyShowsGridItem(showId: 3, title: "The Wire", posterImageUrl: nil, watchProgress: 0.2),
            MyShowsGridItem(showId: 4, title: "Severance", posterImageUrl: nil, watchProgress: 0.5),
        ]
        screen(state: makeState(watchNextGridItems: sampleGridItems, staleGridItems: staleItems))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_GridMode_WithStale")
    }

    func test_MyShowsScreen_EmptyInProgress() {
        screen(state: makeState(emptyText: "Nothing in progress yet. Mark an episode as watched to see it here."))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_EmptyInProgress")
    }

    func test_MyShowsScreen_UpToDate() {
        screen(state: makeState(layout: .list))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_UpToDate")
    }

    func test_MyShowsScreen_CompactMode() {
        screen(state: makeState(layout: .compact, watchNextEpisodes: sampleEpisodes))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_CompactMode")
    }

    func test_MyShowsScreen_DetailedMode() {
        screen(state: makeState(layout: .detailed, watchNextEpisodes: sampleEpisodes))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_DetailedMode")
    }
}
