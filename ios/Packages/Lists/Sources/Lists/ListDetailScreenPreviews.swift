import DesignSystem
import Models
import SwiftUI

private let previewItems: [ShowPosterImage] = [
    ShowPosterImage(showId: 1, title: "Arcane", posterUrl: nil),
    ShowPosterImage(showId: 2, title: "Loki", posterUrl: nil),
    ShowPosterImage(showId: 3, title: "The Bear", posterUrl: nil),
    ShowPosterImage(showId: 4, title: "Severance", posterUrl: nil),
    ShowPosterImage(showId: 5, title: "Shogun", posterUrl: nil),
]

#Preview("Loading") {
    NavigationStack {
        ListDetailScreen(state: ListDetailScreen.State(title: "Watchlist", isLoading: true))
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Empty") {
    NavigationStack {
        ListDetailScreen(
            state: ListDetailScreen.State(
                title: "Watchlist",
                isLoading: false,
                emptyMessage: "No shows in this list yet."
            )
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Content") {
    NavigationStack {
        ListDetailScreen(
            state: ListDetailScreen.State(
                title: "Watchlist",
                isLoading: false,
                items: previewItems,
                retryLabel: "Retry",
                removeButtonLabel: "Remove"
            )
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Loading More") {
    NavigationStack {
        ListDetailScreen(
            state: ListDetailScreen.State(
                title: "Watchlist",
                isLoading: false,
                items: previewItems,
                isLoadingMore: true,
                retryLabel: "Retry",
                removeButtonLabel: "Remove"
            )
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Error") {
    NavigationStack {
        ListDetailScreen(
            state: ListDetailScreen.State(
                title: "Watchlist",
                isLoading: false,
                errorMessage: "Failed to load list",
                dismissErrorLabel: "OK"
            )
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Remove Confirmation") {
    NavigationStack {
        ListDetailScreen(
            state: ListDetailScreen.State(
                title: "Watchlist",
                isLoading: false,
                items: previewItems,
                retryLabel: "Retry",
                removeButtonLabel: "Remove",
                removeConfirmation: ListDetailScreen.RemoveConfirmation(
                    tmdbId: 1,
                    title: "Remove from list?",
                    message: "Remove Arcane from Watchlist?",
                    confirmLabel: "Remove"
                ),
                cancelLabel: "Cancel"
            )
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}
