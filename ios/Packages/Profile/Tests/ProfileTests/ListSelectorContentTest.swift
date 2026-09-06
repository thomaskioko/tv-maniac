import Components
import DesignSystem
import Models
import Profile
import SnapshotTestingLib
import SwiftUI
import XCTest

class ListSelectorContentTest: SnapshotTestCase {
    private let sampleLists: [SwiftListItem] = [
        SwiftListItem(
            listId: 1,
            slug: "favorites",
            name: "Favorites",
            description: "My favorite shows",
            showCountText: "12 shows",
            isShowInList: true
        ),
        SwiftListItem(
            listId: 2,
            slug: "watch-later",
            name: "Watch Later",
            description: "Shows to watch later",
            showCountText: "5 shows",
            isShowInList: false
        ),
        SwiftListItem(
            listId: 3,
            slug: "sci-fi-marathon",
            name: "Sci-Fi Marathon",
            description: nil,
            showCountText: "23 shows",
            isShowInList: true
        ),
    ]

    func test_ListSelector_WithLists() {
        ListSelectorContent(
            state: ListSelectorContent.State(
                title: "Loki",
                posterUrl: nil,
                lists: sampleLists
            ),
            onToggle: { _, _ in },
            onShowCreateField: {},
            onDismissCreateField: {},
            onCreateListNameChanged: { _ in },
            onCreateSubmitted: {},
            onDismiss: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ListSelector_WithLists")
    }

    func test_ListSelector_WithCreateField() {
        ListSelectorContent(
            state: ListSelectorContent.State(
                title: "Loki",
                posterUrl: nil,
                lists: sampleLists,
                showCreateField: true,
                createListName: "My New List"
            ),
            onToggle: { _, _ in },
            onShowCreateField: {},
            onDismissCreateField: {},
            onCreateListNameChanged: { _ in },
            onCreateSubmitted: {},
            onDismiss: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ListSelector_WithCreateField")
    }

    func test_ListSelector_Empty() {
        ListSelectorContent(
            state: ListSelectorContent.State(
                title: "Loki",
                posterUrl: nil,
                lists: []
            ),
            onToggle: { _, _ in },
            onShowCreateField: {},
            onDismissCreateField: {},
            onCreateListNameChanged: { _ in },
            onCreateSubmitted: {},
            onDismiss: {}
        )
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ListSelector_Empty")
    }
}
