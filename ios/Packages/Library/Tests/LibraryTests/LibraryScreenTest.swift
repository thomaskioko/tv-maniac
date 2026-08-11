import Components
import DesignSystem
import Library
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class LibraryScreenTest: SnapshotTestCase {
    private let sampleGridItems: [LibraryGridItem] = [
        LibraryGridItem(showId: 1, title: "Breaking Bad", posterImageUrl: nil),
        LibraryGridItem(showId: 2, title: "Game of Thrones", posterImageUrl: nil),
        LibraryGridItem(showId: 3, title: "The Wire", posterImageUrl: nil),
        LibraryGridItem(showId: 4, title: "Stranger Things", posterImageUrl: nil),
    ]

    private let sampleListItems: [SwiftLibraryItem] = [
        SwiftLibraryItem(
            showId: 1,
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
            showId: 2,
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
            showId: 3,
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
            showId: 4,
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

    private let menuCopy = LayoutMenuCopy(
        grid: "Grid",
        list: "List",
        compact: "Compact",
        detailed: "Detailed",
        premiumSectionTitle: "Premium",
        lockedAccessibilitySuffix: "Locked",
        lockedHint: "Compact and Detailed are Premium layouts. Upgrade to Premium to use these layouts.",
        upgradeActionName: "Upgrade to Premium"
    )

    private func makeState(
        isLoading: Bool = false,
        isEmpty: Bool = false,
        layout: SwiftListStyle = .grid,
        isLayoutLocked: Bool = false,
        gridItems: [LibraryGridItem] = [],
        listItems: [SwiftLibraryItem] = []
    ) -> LibraryScreen.State {
        LibraryScreen.State(
            title: "Library",
            searchPlaceholder: "Search shows",
            emptyText: "No content",
            isLoading: isLoading,
            isRefreshing: false,
            isEmpty: isEmpty,
            layout: layout,
            isLayoutLocked: isLayoutLocked,
            layoutMenuCopy: menuCopy,
            isSearchActive: false,
            query: "",
            gridItems: gridItems,
            listItems: listItems
        )
    }

    private func screen(state: LibraryScreen.State) -> some View {
        LibraryScreen(
            state: state,
            onQueryChanged: { _ in },
            onQueryCleared: {},
            onLayoutSelected: { _ in },
            onUpgradeRequested: {},
            onToggleSearch: {},
            onSortClicked: {},
            onShowClicked: { _ in }
        )
    }

    func test_LibraryScreen_Loading() {
        screen(state: makeState(isLoading: true))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "LibraryScreen_Loading")
    }

    func test_LibraryScreen_Empty() {
        screen(state: makeState(isEmpty: true))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "LibraryScreen_Empty")
    }

    func test_LibraryScreen_GridMode() {
        screen(state: makeState(layout: .grid, gridItems: sampleGridItems))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "LibraryScreen_GridMode")
    }

    func test_LibraryScreen_ListMode() {
        screen(state: makeState(layout: .list, listItems: sampleListItems))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "LibraryScreen_ListMode")
    }

    func test_LibraryScreen_CompactMode() {
        screen(state: makeState(layout: .compact, listItems: sampleListItems))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "LibraryScreen_CompactMode")
    }

    func test_LibraryScreen_DetailedMode() {
        screen(state: makeState(layout: .detailed, listItems: sampleListItems))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "LibraryScreen_DetailedMode")
    }

    func test_LibraryScreen_MenuUnlocked() {
        screen(state: makeState(layout: .grid, isLayoutLocked: false, gridItems: sampleGridItems))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "LibraryScreen_MenuUnlocked")
    }

    func test_LibraryScreen_MenuLocked() {
        screen(state: makeState(layout: .grid, isLayoutLocked: true, gridItems: sampleGridItems))
            .appPreview()
            .assertSnapshot(layout: .defaultDevice, testName: "LibraryScreen_MenuLocked")
    }
}
