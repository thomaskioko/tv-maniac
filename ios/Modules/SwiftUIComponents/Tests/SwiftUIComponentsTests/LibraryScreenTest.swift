import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class LibraryScreenTest: SnapshotTestCase {
    private let sampleGridItems: [LibraryGridItem] = [
        LibraryGridItem(traktId: 1, title: "Breaking Bad", posterImageUrl: nil),
        LibraryGridItem(traktId: 2, title: "Game of Thrones", posterImageUrl: nil),
        LibraryGridItem(traktId: 3, title: "The Wire", posterImageUrl: nil),
        LibraryGridItem(traktId: 4, title: "Stranger Things", posterImageUrl: nil),
    ]

    func test_LibraryScreen_Loading() {
        LibraryScreen(
            title: "Library",
            searchPlaceholder: "Search shows",
            emptyText: "No content",
            isLoading: true,
            isRefreshing: false,
            isEmpty: false,
            isGridMode: true,
            isSearchActive: false,
            query: "",
            gridItems: [],
            listItems: [],
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onToggleListStyle: {},
            onToggleSearch: {},
            onSortClicked: {},
            onShowClicked: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "LibraryScreen_Loading")
    }

    func test_LibraryScreen_Empty() {
        LibraryScreen(
            title: "Library",
            searchPlaceholder: "Search shows",
            emptyText: "No content",
            isLoading: false,
            isRefreshing: false,
            isEmpty: true,
            isGridMode: true,
            isSearchActive: false,
            query: "",
            gridItems: [],
            listItems: [],
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onToggleListStyle: {},
            onToggleSearch: {},
            onSortClicked: {},
            onShowClicked: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "LibraryScreen_Empty")
    }

    func test_LibraryScreen_GridMode() {
        LibraryScreen(
            title: "Library",
            searchPlaceholder: "Search shows",
            emptyText: "No content",
            isLoading: false,
            isRefreshing: false,
            isEmpty: false,
            isGridMode: true,
            isSearchActive: false,
            query: "",
            gridItems: sampleGridItems,
            listItems: [],
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onToggleListStyle: {},
            onToggleSearch: {},
            onSortClicked: {},
            onShowClicked: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "LibraryScreen_GridMode")
    }

    private let sampleListItems: [SwiftLibraryItem] = [
        SwiftLibraryItem(
            traktId: 1,
            title: "Breaking Bad",
            posterUrl: nil,
            year: "2008",
            status: "Ended",
            seasonCount: 5,
            episodeCount: 62,
            rating: 9.5,
            genres: ["Drama", "Crime"],
            watchProviders: []
        ),
        SwiftLibraryItem(
            traktId: 2,
            title: "Game of Thrones",
            posterUrl: nil,
            year: "2011",
            status: "Ended",
            seasonCount: 8,
            episodeCount: 73,
            rating: 9.2,
            genres: ["Drama", "Fantasy"],
            watchProviders: []
        ),
        SwiftLibraryItem(
            traktId: 3,
            title: "The Wire",
            posterUrl: nil,
            year: "2002",
            status: "Ended",
            seasonCount: 5,
            episodeCount: 60,
            rating: 9.3,
            genres: ["Drama", "Crime"],
            watchProviders: []
        ),
        SwiftLibraryItem(
            traktId: 4,
            title: "Stranger Things",
            posterUrl: nil,
            year: "2016",
            status: "Returning Series",
            seasonCount: 4,
            episodeCount: 34,
            rating: 8.7,
            genres: ["Drama", "Fantasy"],
            watchProviders: []
        ),
    ]

    func test_LibraryScreen_ListMode() {
        LibraryScreen(
            title: "Library",
            searchPlaceholder: "Search shows",
            emptyText: "No content",
            isLoading: false,
            isRefreshing: false,
            isEmpty: false,
            isGridMode: false,
            isSearchActive: false,
            query: "",
            gridItems: [],
            listItems: sampleListItems,
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onToggleListStyle: {},
            onToggleSearch: {},
            onSortClicked: {},
            onShowClicked: { _ in }
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "LibraryScreen_ListMode")
    }
}
