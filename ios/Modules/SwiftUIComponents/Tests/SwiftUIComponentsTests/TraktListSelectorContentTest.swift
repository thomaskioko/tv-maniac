import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class TraktListSelectorContentTest: SnapshotTestCase {
    private let sampleLists: [SwiftTraktListItem] = [
        SwiftTraktListItem(
            listId: 1,
            slug: "favorites",
            name: "Favorites",
            description: "My favorite shows",
            itemCount: 12,
            isShowInList: true
        ),
        SwiftTraktListItem(
            listId: 2,
            slug: "watch-later",
            name: "Watch Later",
            description: "Shows to watch later",
            itemCount: 5,
            isShowInList: false
        ),
        SwiftTraktListItem(
            listId: 3,
            slug: "sci-fi-marathon",
            name: "Sci-Fi Marathon",
            description: nil,
            itemCount: 23,
            isShowInList: true
        ),
    ]

    func test_TraktListSelector_WithLists() {
        TraktListSelectorContent(
            title: "Loki",
            posterUrl: nil,
            traktLists: sampleLists,
            onToggle: { _, _ in },
            onShowCreateField: {},
            onDismissCreateField: {},
            onCreateListNameChanged: { _ in },
            onCreateSubmitted: {},
            onDismiss: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "TraktListSelector_WithLists")
    }

    func test_TraktListSelector_WithCreateField() {
        TraktListSelectorContent(
            title: "Loki",
            posterUrl: nil,
            traktLists: sampleLists,
            showCreateField: true,
            createListName: "My New List",
            onToggle: { _, _ in },
            onShowCreateField: {},
            onDismissCreateField: {},
            onCreateListNameChanged: { _ in },
            onCreateSubmitted: {},
            onDismiss: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "TraktListSelector_WithCreateField")
    }

    func test_TraktListSelector_Empty() {
        TraktListSelectorContent(
            title: "Loki",
            posterUrl: nil,
            traktLists: [],
            onToggle: { _, _ in },
            onShowCreateField: {},
            onDismissCreateField: {},
            onCreateListNameChanged: { _ in },
            onCreateSubmitted: {},
            onDismiss: {}
        )
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "TraktListSelector_Empty")
    }
}
