import SwiftUI

#Preview("Loading") {
    ThemedPreview {
        SearchScreen(
            title: "Search",
            state: .loading,
            query: .constant(""),
            searchPlaceholder: "Enter Show Title",
            emptyResultsMessage: "No results found",
            retryButtonText: "Retry",
            onShowClicked: { _ in },
            onRetry: {},
            onBack: {}
        )
    }
    .preferredColorScheme(.dark)
}

#Preview("Browsing Genres") {
    ThemedPreview {
        SearchScreen(
            title: "Search",
            state: .browsingGenres(
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
            query: .constant(""),
            searchPlaceholder: "Enter Show Title",
            emptyResultsMessage: "No results found",
            retryButtonText: "Retry",
            selectedCategory: "Popular",
            categories: ["Popular", "Trending", "Top Rated"],
            onShowClicked: { _ in },
            onRetry: {},
            onBack: {}
        )
    }
    .preferredColorScheme(.dark)
}

#Preview("Search Results") {
    ThemedPreview {
        SearchScreen(
            title: "Search",
            state: .searchResults(
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
            query: .constant("penguin"),
            searchPlaceholder: "Enter Show Title",
            emptyResultsMessage: "No results found",
            retryButtonText: "Retry",
            onShowClicked: { _ in },
            onRetry: {},
            onBack: {}
        )
    }
    .preferredColorScheme(.dark)
}

#Preview("Empty Results") {
    ThemedPreview {
        SearchScreen(
            title: "Search",
            state: .empty,
            query: .constant("xyzabc"),
            searchPlaceholder: "Enter Show Title",
            emptyResultsMessage: "No results found",
            retryButtonText: "Retry",
            onShowClicked: { _ in },
            onRetry: {},
            onBack: {}
        )
    }
    .preferredColorScheme(.dark)
}

#Preview("Error") {
    ThemedPreview {
        SearchScreen(
            title: "Search",
            state: .error(message: "Something went wrong. Please try again."),
            query: .constant(""),
            searchPlaceholder: "Enter Show Title",
            emptyResultsMessage: "No results found",
            retryButtonText: "Retry",
            onShowClicked: { _ in },
            onRetry: {},
            onBack: {}
        )
    }
    .preferredColorScheme(.dark)
}
