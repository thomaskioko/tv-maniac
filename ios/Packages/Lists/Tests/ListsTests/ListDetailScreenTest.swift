import Components
import DesignSystem
@testable import Lists
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class ListDetailScreenTest: SnapshotTestCase {
    private let sampleItems: [ShowPosterImage] = [
        ShowPosterImage(showId: 1, title: "Arcane", posterUrl: nil),
        ShowPosterImage(showId: 2, title: "Loki", posterUrl: nil),
        ShowPosterImage(showId: 3, title: "The Bear", posterUrl: nil),
        ShowPosterImage(showId: 4, title: "Severance", posterUrl: nil),
    ]

    func test_ListDetailScreen_Loading() {
        let view = makeScreen(state: ListDetailScreen.State(title: "Watchlist", isLoading: true))
        view.assertSnapshot(layout: .defaultDevice, testName: "ListDetailScreen_Loading")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ListDetailScreen_Loading")
    }

    func test_ListDetailScreen_Empty() {
        let view = makeScreen(
            state: ListDetailScreen.State(
                title: "Watchlist",
                isLoading: false,
                emptyMessage: "No shows in this list yet."
            )
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "ListDetailScreen_Empty")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ListDetailScreen_Empty")
    }

    func test_ListDetailScreen_Content() {
        let view = makeScreen(
            state: ListDetailScreen.State(
                title: "Watchlist",
                isLoading: false,
                items: sampleItems,
                retryLabel: "Retry",
                removeButtonLabel: "Remove",
                renameLabel: "Rename",
                deleteLabel: "Delete list"
            )
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "ListDetailScreen_Content")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ListDetailScreen_Content")
    }

    func test_ListDetailScreen_Error() {
        let view = makeScreen(
            state: ListDetailScreen.State(
                title: "Watchlist",
                isLoading: false,
                errorMessage: "Failed to load list",
                dismissErrorLabel: "OK"
            )
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "ListDetailScreen_Error")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "ListDetailScreen_Error")
    }

    private func makeScreen(state: ListDetailScreen.State) -> some View {
        NavigationStack {
            ListDetailScreen(state: state, backButtonAccessibilityLabel: "Back")
        }
        .appPreview()
    }
}
