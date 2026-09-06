import Components
import DesignSystem
@testable import Lists
import SnapshotTestingLib
import SwiftUI
import XCTest

class ListsScreenTest: SnapshotTestCase {
    private let sampleLists: [ListCollageItem] = [
        ListCollageItem(id: 1, name: "Watchlist", itemCountLabel: "24 shows", posterUrls: ["a", "b", "c", "d"]),
        ListCollageItem(id: 2, name: "Favorites", itemCountLabel: "12 shows", posterUrls: ["e", "f", "g"]),
        ListCollageItem(id: 3, name: "Weekend Binge", itemCountLabel: "2 shows", posterUrls: ["h"]),
        ListCollageItem(id: 4, name: "New List", itemCountLabel: "0 shows", posterUrls: []),
    ]

    func test_ListsScreen_Loading() {
        makeScreen(state: ListsScreen.State(title: "Lists", isLoading: true))
            .assertSnapshot(layout: .defaultDevice, testName: "ListsScreen_Loading")
    }

    func test_ListsScreen_Empty() {
        makeScreen(
            state: ListsScreen.State(
                title: "Lists",
                isLoading: false,
                emptyMessage: "You don't have any lists yet."
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "ListsScreen_Empty")
    }

    func test_ListsScreen_Content() {
        makeScreen(
            state: ListsScreen.State(
                title: "Lists",
                isLoading: false,
                lists: sampleLists
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "ListsScreen_Content")
    }

    func test_ListsScreen_Error() {
        makeScreen(
            state: ListsScreen.State(
                title: "Lists",
                isLoading: false,
                errorMessage: "Failed to load lists"
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "ListsScreen_Error")
    }

    private func makeScreen(state: ListsScreen.State) -> some View {
        NavigationStack {
            ListsScreen(state: state, backButtonAccessibilityLabel: "Back")
        }
        .appPreview()
    }
}
