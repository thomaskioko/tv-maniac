import DesignSystem
import Models
import SwiftUI

private let previewMenuCopy = LayoutMenuCopy(
    grid: "Grid",
    list: "List",
    compact: "Compact",
    detailed: "Detailed",
    premiumSectionTitle: "Premium",
    lockedAccessibilitySuffix: "Locked",
    lockedHint: "Compact and Detailed are Premium layouts. Upgrade to Premium to use these layouts.",
    upgradeActionName: "Upgrade to Premium"
)

private let previewListItems: [SwiftLibraryItem] = [
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
]

private func previewState(layout: SwiftListStyle, isLayoutLocked: Bool = false) -> LibraryScreen.State {
    LibraryScreen.State(
        title: "Library",
        searchPlaceholder: "Search shows",
        emptyText: "No content",
        isLoading: false,
        isRefreshing: false,
        isEmpty: false,
        layout: layout,
        isLayoutLocked: isLayoutLocked,
        layoutMenuCopy: previewMenuCopy,
        isSearchActive: false,
        query: "",
        gridItems: [],
        listItems: previewListItems
    )
}

#Preview("Compact") {
    LibraryScreen(
        state: previewState(layout: .compact),
        onQueryChanged: { _ in },
        onQueryCleared: {},
        onLayoutSelected: { _ in },
        onUpgradeRequested: {},
        onToggleSearch: {},
        onSortClicked: {},
        onShowClicked: { _ in }
    )
    .appPreview()
}

#Preview("Detailed") {
    LibraryScreen(
        state: previewState(layout: .detailed),
        onQueryChanged: { _ in },
        onQueryCleared: {},
        onLayoutSelected: { _ in },
        onUpgradeRequested: {},
        onToggleSearch: {},
        onSortClicked: {},
        onShowClicked: { _ in }
    )
    .appPreview()
}

#Preview("Detailed - Locked") {
    LibraryScreen(
        state: previewState(layout: .detailed, isLayoutLocked: true),
        onQueryChanged: { _ in },
        onQueryCleared: {},
        onLayoutSelected: { _ in },
        onUpgradeRequested: {},
        onToggleSearch: {},
        onSortClicked: {},
        onShowClicked: { _ in }
    )
    .appPreview()
}
