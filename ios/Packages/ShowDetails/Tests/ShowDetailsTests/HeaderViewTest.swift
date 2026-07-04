import Components
import DesignSystem
import Models
import ShowDetails
import SnapshotTestingLib
import SwiftUI
import XCTest

class HeaderViewTest: SnapshotTestCase {
    func test_HeaderView() {
        HeaderView(
            title: "Arcane",
            overview: "Set in Utopian Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League of Legends champions and the power that will tear them apart.",
            backdropImageUrl: "",
            status: "Continuing",
            year: "2024",
            language: "EN",
            communityRating: 4.8,
            communityVotes: 12500,
            seasonCount: 2,
            seasonCountFormat: { count in count == 1 ? "\(count) Season" : "\(count) Seasons" },
            progress: 0
        )
        .appPreview()
        .assertSnapshot(testName: "HeaderView")
    }

    func test_HeaderView_NoCommunityRating() {
        HeaderView(
            title: "Arcane",
            overview: "Set in Utopian Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League of Legends champions and the power that will tear them apart.",
            backdropImageUrl: "",
            status: "Continuing",
            year: "2024",
            language: "EN",
            communityRating: nil,
            communityVotes: nil,
            seasonCount: 2,
            seasonCountFormat: { count in count == 1 ? "\(count) Season" : "\(count) Seasons" },
            progress: 0
        )
        .appPreview()
        .assertSnapshot(testName: "HeaderView_NoCommunityRating")
    }
}
