import Components
import DesignSystem
import Models
import Search
import SnapshotTestingLib
import SwiftUI
import XCTest

class SearchScreenTest: SnapshotTestCase {
    private let sampleGenres: [SwiftGenreRow] = [
        SwiftGenreRow(
            id: "action",
            name: "Action",
            subtitle: "Non-stop thrills and action",
            shows: [
                .init(showId: 1, title: "Arcane", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                .init(showId: 2, title: "The Penguin", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                .init(showId: 3, title: "Reacher", posterUrl: nil, backdropUrl: nil, inLibrary: false),
            ]
        ),
        SwiftGenreRow(
            id: "drama",
            name: "Drama",
            subtitle: "Compelling stories",
            shows: [
                .init(showId: 4, title: "Kaos", posterUrl: nil, backdropUrl: nil, inLibrary: false),
                .init(showId: 5, title: "One Piece", posterUrl: nil, backdropUrl: nil, inLibrary: false),
            ]
        ),
    ]

    private let sampleResults: [SwiftSearchShow] = [
        .init(
            tmdbId: 44234, showId: 44234, title: "The Penguin",
            overview: "Follow Oswald Oz Cobb's quest for control as he seeks to fill the power vacuum.",
            status: "Ended",
            imageUrl: nil,
            year: "2024", voteAverage: 8.5
        ),
        .init(
            tmdbId: 1234, showId: 1234, title: "Kaos",
            overview: "A renegade fighter battles a powerful robot for vital data.",
            status: "Ended",
            imageUrl: nil,
            year: "2024", voteAverage: 7.2
        ),
    ]

    func test_SearchScreen_Loading() {
        let view = makeScreen(state: .loading)
        view.assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_Loading")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SearchScreen_Loading")
    }

    func test_SearchScreen_BrowsingGenres() {
        let view = makeScreen(state: .browsingGenres(genres: sampleGenres, isRefreshing: false))
        view.assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_BrowsingGenres")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SearchScreen_BrowsingGenres")
    }

    func test_SearchScreen_BrowsingGenres_Refreshing() {
        let view = makeScreen(state: .browsingGenres(genres: sampleGenres, isRefreshing: true))
        view.assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_BrowsingGenres_Refreshing")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SearchScreen_BrowsingGenres_Refreshing")
    }

    func test_SearchScreen_SearchResults() {
        let view = makeScreen(state: .searchResults(results: sampleResults, isUpdating: false), query: "penguin")
        view.assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_SearchResults")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SearchScreen_SearchResults")
    }

    func test_SearchScreen_SearchResults_Updating() {
        let view = makeScreen(state: .searchResults(results: sampleResults, isUpdating: true), query: "penguin")
        view.assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_SearchResults_Updating")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SearchScreen_SearchResults_Updating")
    }

    func test_SearchScreen_Empty() {
        let view = makeScreen(state: .empty, query: "xyzabc")
        view.assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_Empty")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SearchScreen_Empty")
    }

    func test_SearchScreen_Error() {
        let view = makeScreen(state: .error(message: "Something went wrong. Please try again."))
        view.assertSnapshot(layout: .defaultDevice, testName: "SearchScreen_Error")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "SearchScreen_Error")
    }

    private func makeScreen(
        state screenState: SearchScreenState,
        query: String = "",
        selectedCategory: String = "Popular",
        categories: [String] = ["Popular", "Trending", "Top Rated", "Most Watched"]
    ) -> some View {
        SearchScreen(
            state: SearchScreen.State(
                title: "Search",
                screenState: screenState,
                searchPlaceholder: "Enter Show Title",
                emptyResultsMessage: "No results found",
                retryButtonText: "Retry",
                selectedCategory: selectedCategory,
                categories: categories,
                categoryTitle: "Category"
            ),
            query: .constant(query),
            onShowClicked: { _ in },
            onRetry: {},
            onBack: {},
            onCategoryChanged: { _ in }
        )
        .appPreview()
    }
}
