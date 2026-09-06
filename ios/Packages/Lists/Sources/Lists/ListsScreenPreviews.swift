import Components
import DesignSystem
import SwiftUI

private let previewLists: [ListCollageItem] = [
    ListCollageItem(id: 1, name: "Watchlist", itemCountLabel: "24 shows", posterUrls: ["a", "b", "c", "d"]),
    ListCollageItem(id: 2, name: "Favorites", itemCountLabel: "12 shows", posterUrls: ["e", "f", "g"]),
    ListCollageItem(id: 3, name: "Weekend Binge", itemCountLabel: "2 shows", posterUrls: ["h"]),
    ListCollageItem(id: 4, name: "New List", itemCountLabel: "0 shows", posterUrls: []),
]

#Preview("Loading") {
    NavigationStack {
        ListsScreen(state: ListsScreen.State(title: "Lists", isLoading: true))
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Empty") {
    NavigationStack {
        ListsScreen(
            state: ListsScreen.State(
                title: "Lists",
                isLoading: false,
                emptyMessage: "You don't have any lists yet."
            )
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Content") {
    NavigationStack {
        ListsScreen(
            state: ListsScreen.State(
                title: "Lists",
                isLoading: false,
                lists: previewLists
            )
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Error") {
    NavigationStack {
        ListsScreen(
            state: ListsScreen.State(
                title: "Lists",
                isLoading: false,
                errorMessage: "Failed to load lists"
            )
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}
