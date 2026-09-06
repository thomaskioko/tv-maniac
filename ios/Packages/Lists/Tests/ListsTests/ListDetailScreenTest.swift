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
        makeScreen(state: ListDetailScreen.State(title: "Watchlist", isLoading: true))
            .assertSnapshot(layout: .defaultDevice, testName: "ListDetailScreen_Loading")
    }

    func test_ListDetailScreen_Empty() {
        makeScreen(
            state: ListDetailScreen.State(
                title: "Watchlist",
                isLoading: false,
                emptyMessage: "No shows in this list yet."
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "ListDetailScreen_Empty")
    }

    func test_ListDetailScreen_Content() {
        makeScreen(
            state: ListDetailScreen.State(
                title: "Watchlist",
                isLoading: false,
                items: sampleItems,
                retryLabel: "Retry",
                removeButtonLabel: "Remove"
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "ListDetailScreen_Content")
    }

    func test_ListDetailScreen_Error() {
        makeScreen(
            state: ListDetailScreen.State(
                title: "Watchlist",
                isLoading: false,
                errorMessage: "Failed to load list",
                dismissErrorLabel: "OK"
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "ListDetailScreen_Error")
    }

    private func makeScreen(state: ListDetailScreen.State) -> some View {
        NavigationStack {
            ListDetailScreen(state: state, backButtonAccessibilityLabel: "Back")
        }
        .appPreview()
    }
}
