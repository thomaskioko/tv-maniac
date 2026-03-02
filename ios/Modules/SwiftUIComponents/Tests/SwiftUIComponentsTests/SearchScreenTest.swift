import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class SearchScreenTest: SnapshotTestCase {
    private let sampleGenres: [SwiftGenreRow] = [
        SwiftGenreRow(
            id: "action",
            name: "Action",
            subtitle: "Non-stop thrills and action",
            shows: [
                .init(traktId: 1, title: "Arcane", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                .init(traktId: 2, title: "The Penguin", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                .init(traktId: 3, title: "Reacher", posterUrl: nil, backdropUrl: nil, inLibrary: false),
            ]
        ),
        SwiftGenreRow(
            id: "drama",
            name: "Drama",
            subtitle: "Compelling stories",
            shows: [
                .init(traktId: 4, title: "Kaos", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                .init(traktId: 5, title: "One Piece", posterUrl: nil, backdropUrl: nil, inLibrary: false),
            ]
        ),
    ]

    private let sampleResults: [SwiftSearchShow] = [
        .init(
            tmdbId: 44234, traktId: 44234, title: "The Penguin",
            overview: "Follow Oswald Oz Cobb's quest for control as he seeks to fill the power vacuum.",
            status: "Ended",
            imageUrl: nil,
            year: "2024", voteAverage: 8.5
        ),
        .init(
            tmdbId: 1234, traktId: 1234, title: "Kaos",
            overview: "A renegade fighter battles a powerful robot for vital data.",
            status: "Ended",
            imageUrl: nil,
            year: "2024", voteAverage: 7.2
        ),
    ]

    func test_SearchScreen_Loading() {
        makeScreen(state: .loading)
            .assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_Loading")
    }

    func test_SearchScreen_BrowsingGenres() {
        makeScreen(state: .browsingGenres(genres: sampleGenres, isRefreshing: false))
            .assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_BrowsingGenres")
    }

    func test_SearchScreen_BrowsingGenres_Refreshing() {
        makeScreen(state: .browsingGenres(genres: sampleGenres, isRefreshing: true))
            .assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_BrowsingGenres_Refreshing")
    }

    func test_SearchScreen_SearchResults() {
        makeScreen(state: .searchResults(results: sampleResults, isUpdating: false), query: "penguin")
            .assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_SearchResults")
    }

    func test_SearchScreen_SearchResults_Updating() {
        makeScreen(state: .searchResults(results: sampleResults, isUpdating: true), query: "penguin")
            .assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_SearchResults_Updating")
    }

    func test_SearchScreen_Empty() {
        makeScreen(state: .empty, query: "xyzabc")
            .assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_Empty")
    }

    func test_SearchScreen_Error() {
        makeScreen(state: .error(message: "Something went wrong. Please try again."))
            .assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_Error")
    }

    private func makeScreen(
        state: SearchScreenState,
        query: String = "",
        selectedCategory: String = "Popular",
        categories: [String] = ["Popular", "Trending", "Top Rated", "Most Watched"]
    ) -> some View {
        SearchScreen(
            title: "Search",
            state: state,
            query: .constant(query),
            searchPlaceholder: "Enter Show Title",
            emptyResultsMessage: "No results found",
            retryButtonText: "Retry",
            selectedCategory: selectedCategory,
            categories: categories,
            categoryTitle: "Category",
            onShowClicked: { _ in },
            onRetry: {},
            onBack: {},
            onCategoryChanged: { _ in }
        )
        .themedPreview()
    }
}
