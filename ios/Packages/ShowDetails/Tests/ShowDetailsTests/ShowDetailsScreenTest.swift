import Components
import DesignSystem
import Models
import ShowDetails
import SnapshotTestingLib
import SwiftUI
import XCTest

class ShowDetailsScreenTest: SnapshotTestCase {
    private let sampleState = ShowDetailsScreen<EmptyView>.State(
        title: "The Last of Us",
        overview: "Twenty years after modern civilization has been destroyed, Joel is hired to smuggle Ellie out of an oppressive quarantine zone.",
        backdropImageUrl: nil,
        status: "Returning Series",
        year: "2023",
        language: "en",
        communityRating: 8.8,
        communityVotes: 15000,
        userRating: 9,
        numberOfSeasons: 2,
        isRefreshing: false
    )

    func test_ShowDetailsScreen_Default() {
        ShowDetailsScreen(
            state: sampleState,
            toast: .constant(nil),
            seasonCountFormat: { "\($0) Seasons" },
            onBack: {},
            onRefresh: {}
        ) {
            EmptyView()
        }
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ShowDetailsScreen_Default")
    }

    func test_ShowDetailsScreen_Refreshing() {
        ShowDetailsScreen(
            state: ShowDetailsScreen<EmptyView>.State(
                title: "The Last of Us",
                overview: "Twenty years after modern civilization has been destroyed.",
                backdropImageUrl: nil,
                status: "Returning Series",
                year: "2023",
                language: "en",
                communityRating: 8.8,
                communityVotes: 15000,
                userRating: 9,
                numberOfSeasons: 2,
                isRefreshing: true
            ),
            toast: .constant(nil),
            seasonCountFormat: { "\($0) Seasons" },
            onBack: {},
            onRefresh: {}
        ) {
            EmptyView()
        }
        .appPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "ShowDetailsScreen_Refreshing")
    }
}
