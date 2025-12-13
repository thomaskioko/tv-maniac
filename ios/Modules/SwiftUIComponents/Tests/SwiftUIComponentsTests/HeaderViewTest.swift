import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
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
            rating: 4.8,
            seasonCount: 2,
            seasonCountFormat: { count in count == 1 ? "\(count) Season" : "\(count) Seasons" },
            progress: 0
        )
        .themedPreview()
        .assertSnapshot(testName: "HeaderView")
    }
}
