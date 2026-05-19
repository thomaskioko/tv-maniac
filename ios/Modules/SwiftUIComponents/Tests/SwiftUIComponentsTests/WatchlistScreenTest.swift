import DesignSystem
import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class WatchlistScreenTest: SnapshotTestCase {
    private let sampleGridItems: [WatchlistGridItem] = [
        WatchlistGridItem(traktId: 1, title: "Breaking Bad", posterImageUrl: nil, watchProgress: 0.7),
        WatchlistGridItem(traktId: 2, title: "Game of Thrones", posterImageUrl: nil, watchProgress: 0.3),
    ]

    func test_WatchlistScreen_Loading() {
        WatchlistScreen(
            state: WatchlistScreen.State(
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
            onMarkWatched: { _ in }
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "WatchlistScreen_Loading")
    }

    func test_WatchlistScreen_GridMode() {
        WatchlistScreen(
            state: WatchlistScreen.State(
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
            onMarkWatched: { _ in }
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "WatchlistScreen_GridMode")
    }

    func test_WatchlistScreen_GridMode_WithStale() {
        let staleItems: [WatchlistGridItem] = [
            WatchlistGridItem(traktId: 3, title: "The Wire", posterImageUrl: nil, watchProgress: 0.2),
            WatchlistGridItem(traktId: 4, title: "Severance", posterImageUrl: nil, watchProgress: 0.5),
        ]
        WatchlistScreen(
            state: WatchlistScreen.State(
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
            onMarkWatched: { _ in }
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "WatchlistScreen_GridMode_WithStale")
    }

    func test_WatchlistScreen_EmptyInProgress() {
        WatchlistScreen(
            state: WatchlistScreen.State(
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
            onMarkWatched: { _ in }
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "WatchlistScreen_EmptyInProgress")
    }

    func test_WatchlistScreen_UpToDate() {
        WatchlistScreen(
            state: WatchlistScreen.State(
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
            onMarkWatched: { _ in }
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "WatchlistScreen_UpToDate")
    }
}
