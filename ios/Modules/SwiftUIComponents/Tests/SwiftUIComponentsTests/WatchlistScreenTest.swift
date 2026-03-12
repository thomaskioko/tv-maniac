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
            staleEpisodes: [],
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onToggleListStyle: {},
            onToggleSearch: {},
            onShowClicked: { _ in },
            onEpisodeClicked: { _, _ in },
            onShowTitleClicked: { _ in },
            onMarkWatched: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "WatchlistScreen_Loading")
    }

    func test_WatchlistScreen_GridMode() {
        WatchlistScreen(
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
            staleEpisodes: [],
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onToggleListStyle: {},
            onToggleSearch: {},
            onShowClicked: { _ in },
            onEpisodeClicked: { _, _ in },
            onShowTitleClicked: { _ in },
            onMarkWatched: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "WatchlistScreen_GridMode")
    }

    func test_WatchlistScreen_UpToDate() {
        WatchlistScreen(
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
            staleEpisodes: [],
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onToggleListStyle: {},
            onToggleSearch: {},
            onShowClicked: { _ in },
            onEpisodeClicked: { _, _ in },
            onShowTitleClicked: { _ in },
            onMarkWatched: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "WatchlistScreen_UpToDate")
    }
}
