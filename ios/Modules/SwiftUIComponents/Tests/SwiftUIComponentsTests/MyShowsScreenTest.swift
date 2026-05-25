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

    func test_MyShowsScreen_Loading() {
        MyShowsScreen(
            state: MyShowsScreen.State(
                title: "Watchlist",
                searchPlaceholder: "Search",
                emptyText: "No content",
                upToDateText: "Up to date",
                listStyleLabel: "List style",
                searchLabel: "Search",
                sortLabel: "Sort",
                upNextSectionTitle: "Up Next",
                staleSectionTitle: "Not watched for a while",
                premiereLabel: "Premiere",
                newLabel: "New",
                isLoading: true,
                isGridMode: true,
                isSearchActive: false,
                query: "",
                watchNextGridItems: [],
                staleGridItems: [],
                watchNextEpisodes: [],
                staleEpisodes: []
            ),
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onToggleListStyle: {},
            onToggleSearch: {},
            onSortClicked: {},
            onShowClicked: { _ in },
            onEpisodeClicked: { _, _ in },
            onShowTitleClicked: { _ in },
            onMarkWatched: { _ in },
            onRefresh: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_Loading")
    }

    func test_MyShowsScreen_GridMode() {
        MyShowsScreen(
            state: MyShowsScreen.State(
                title: "Watchlist",
                searchPlaceholder: "Search",
                emptyText: "No content",
                upToDateText: "Up to date",
                listStyleLabel: "List style",
                searchLabel: "Search",
                sortLabel: "Sort",
                upNextSectionTitle: "Up Next",
                staleSectionTitle: "Not watched for a while",
                premiereLabel: "Premiere",
                newLabel: "New",
                isLoading: false,
                isGridMode: true,
                isSearchActive: false,
                query: "",
                watchNextGridItems: sampleGridItems,
                staleGridItems: [],
                watchNextEpisodes: [],
                staleEpisodes: []
            ),
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onToggleListStyle: {},
            onToggleSearch: {},
            onSortClicked: {},
            onShowClicked: { _ in },
            onEpisodeClicked: { _, _ in },
            onShowTitleClicked: { _ in },
            onMarkWatched: { _ in },
            onRefresh: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_GridMode")
    }

    func test_MyShowsScreen_GridMode_WithStale() {
        let staleItems: [MyShowsGridItem] = [
            MyShowsGridItem(traktId: 3, title: "The Wire", posterImageUrl: nil, watchProgress: 0.2),
            MyShowsGridItem(traktId: 4, title: "Severance", posterImageUrl: nil, watchProgress: 0.5),
        ]
        MyShowsScreen(
            state: MyShowsScreen.State(
                title: "Watchlist",
                searchPlaceholder: "Search",
                emptyText: "No content",
                upToDateText: "Up to date",
                listStyleLabel: "List style",
                searchLabel: "Search",
                sortLabel: "Sort",
                upNextSectionTitle: "Up Next",
                staleSectionTitle: "Not watched for a while",
                premiereLabel: "Premiere",
                newLabel: "New",
                isLoading: false,
                isGridMode: true,
                isSearchActive: false,
                query: "",
                watchNextGridItems: sampleGridItems,
                staleGridItems: staleItems,
                watchNextEpisodes: [],
                staleEpisodes: []
            ),
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onToggleListStyle: {},
            onToggleSearch: {},
            onSortClicked: {},
            onShowClicked: { _ in },
            onEpisodeClicked: { _, _ in },
            onShowTitleClicked: { _ in },
            onMarkWatched: { _ in },
            onRefresh: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_GridMode_WithStale")
    }

    func test_MyShowsScreen_EmptyInProgress() {
        MyShowsScreen(
            state: MyShowsScreen.State(
                title: "Watchlist",
                searchPlaceholder: "Search",
                emptyText: "Nothing in progress yet. Mark an episode as watched to see it here.",
                upToDateText: "Up to date",
                listStyleLabel: "List style",
                searchLabel: "Search",
                sortLabel: "Sort",
                upNextSectionTitle: "Up Next",
                staleSectionTitle: "Not watched for a while",
                premiereLabel: "Premiere",
                newLabel: "New",
                isLoading: false,
                isGridMode: true,
                isSearchActive: false,
                query: "",
                watchNextGridItems: [],
                staleGridItems: [],
                watchNextEpisodes: [],
                staleEpisodes: []
            ),
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onToggleListStyle: {},
            onToggleSearch: {},
            onSortClicked: {},
            onShowClicked: { _ in },
            onEpisodeClicked: { _, _ in },
            onShowTitleClicked: { _ in },
            onMarkWatched: { _ in },
            onRefresh: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_EmptyInProgress")
    }

    func test_MyShowsScreen_UpToDate() {
        MyShowsScreen(
            state: MyShowsScreen.State(
                title: "Watchlist",
                searchPlaceholder: "Search",
                emptyText: "No content",
                upToDateText: "Up to date",
                listStyleLabel: "List style",
                searchLabel: "Search",
                sortLabel: "Sort",
                upNextSectionTitle: "Up Next",
                staleSectionTitle: "Not watched for a while",
                premiereLabel: "Premiere",
                newLabel: "New",
                isLoading: false,
                isGridMode: false,
                isSearchActive: false,
                query: "",
                watchNextGridItems: [],
                staleGridItems: [],
                watchNextEpisodes: [],
                staleEpisodes: []
            ),
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onToggleListStyle: {},
            onToggleSearch: {},
            onSortClicked: {},
            onShowClicked: { _ in },
            onEpisodeClicked: { _, _ in },
            onShowTitleClicked: { _ in },
            onMarkWatched: { _ in },
            onRefresh: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "MyShowsScreen_UpToDate")
    }
}
