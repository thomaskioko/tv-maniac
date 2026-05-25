import DesignSystem
import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class MyShowsScreenTest: SnapshotTestCase {
    private let sampleGridItems: [MyShowsGridItem] = [
        MyShowsGridItem(traktId: 1, title: "Breaking Bad", posterImageUrl: nil, watchProgress: 0.7),
        MyShowsGridItem(traktId: 2, title: "Game of Thrones", posterImageUrl: nil, watchProgress: 0.3),
    ]

    private func makeState(
        emptyText: String = "No content",
        isLoading: Bool = false,
        isGridMode: Bool = true,
        watchNextGridItems: [MyShowsGridItem] = [],
        staleGridItems: [MyShowsGridItem] = []
    ) -> MyShowsScreen.State {
        MyShowsScreen.State(
            emptyText: emptyText,
            upToDateText: "Up to date",
            upNextSectionTitle: "Up Next",
            staleSectionTitle: "Not watched for a while",
            premiereLabel: "Premiere",
            newLabel: "New",
            isLoading: isLoading,
            isGridMode: isGridMode,
            query: "",
            watchNextGridItems: watchNextGridItems,
            staleGridItems: staleGridItems,
            watchNextEpisodes: [],
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
            MyShowsGridItem(traktId: 3, title: "The Wire", posterImageUrl: nil, watchProgress: 0.2),
            MyShowsGridItem(traktId: 4, title: "Severance", posterImageUrl: nil, watchProgress: 0.5),
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
        screen(state: makeState(isGridMode: false))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_UpToDate")
    }
}
