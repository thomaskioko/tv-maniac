import Components
import DesignSystem
import Models
import SwiftUI

#Preview("Loading") {
    SearchScreen(
        state: SearchScreen.State(
            title: "Search",
            screenState: .loading,
            searchPlaceholder: "Enter Show Title",
            emptyResultsMessage: "No results found",
            retryButtonText: "Retry"
        ),
        query: .constant(""),
        onShowClicked: { _ in },
        onRetry: {},
        onBack: {}
    )
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Browsing Genres") {
    SearchScreen(
        state: SearchScreen.State(
            title: "Search",
            screenState: .browsingGenres(
                genres: [
                    SwiftGenreRow(
                        id: "action",
                        name: "Action",
                        subtitle: "High-octane thrills",
                        shows: [
                            .init(traktId: 1, title: "Arcane", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                            .init(traktId: 2, title: "The Penguin", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                        ]
                    ),
                    SwiftGenreRow(
                        id: "drama",
                        name: "Drama",
                        subtitle: "Compelling stories",
                        shows: [
                            .init(traktId: 3, title: "Kaos", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                            .init(traktId: 4, title: "One Piece", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                        ]
                    ),
                ],
                isRefreshing: false
            ),
            searchPlaceholder: "Enter Show Title",
            emptyResultsMessage: "No results found",
            retryButtonText: "Retry",
            selectedCategory: "Popular",
            categories: ["Popular", "Trending", "Top Rated"]
        ),
        query: .constant(""),
        onShowClicked: { _ in },
        onRetry: {},
        onBack: {}
    )
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Search Results") {
    SearchScreen(
        state: SearchScreen.State(
            title: "Search",
            screenState: .searchResults(
                results: [
                    .init(
                        tmdbId: 44234, traktId: 44234, title: "The Penguin",
                        overview: "Follow Oswald Oz Cobb's quest for control.",
                        status: "Ended",
                        imageUrl: "https://image.tmdb.org/t/p/w780/VSRmtRlYgd0pBISf7d34TAwWgB.jpg",
                        year: "2024", voteAverage: 8.5
                    ),
                    .init(
                        tmdbId: 1234, traktId: 1234, title: "Kaos",
                        overview: "A renegade fighter battles a powerful robot.",
                        status: "Ended",
                        imageUrl: "https://image.tmdb.org/t/p/w780/9Piw6Zju39bn3enIDLZzPfjMTBR.jpg",
                        year: "2024", voteAverage: 7.2
                    ),
                ],
                isUpdating: false
            ),
            searchPlaceholder: "Enter Show Title",
            emptyResultsMessage: "No results found",
            retryButtonText: "Retry"
        ),
        query: .constant("penguin"),
        onShowClicked: { _ in },
        onRetry: {},
        onBack: {}
    )
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Empty Results") {
    SearchScreen(
        state: SearchScreen.State(
            title: "Search",
            screenState: .empty,
            searchPlaceholder: "Enter Show Title",
            emptyResultsMessage: "No results found",
            retryButtonText: "Retry"
        ),
        query: .constant("xyzabc"),
        onShowClicked: { _ in },
        onRetry: {},
        onBack: {}
    )
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Error") {
    SearchScreen(
        state: SearchScreen.State(
            title: "Search",
            screenState: .error(message: "Something went wrong. Please try again."),
            searchPlaceholder: "Enter Show Title",
            emptyResultsMessage: "No results found",
            retryButtonText: "Retry"
        ),
        query: .constant(""),
        onShowClicked: { _ in },
        onRetry: {},
        onBack: {}
    )
    .appPreview()
    .preferredColorScheme(.dark)
}
