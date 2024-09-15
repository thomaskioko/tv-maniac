import SnapshotTestingLib
import SwiftUI
import TvManiacUI
import XCTest

class HeaderViewTest: XCTestCase {
    func test_HeaderView() {
        HeaderView(
            title: "Arcane",
            overview: "Set in Utopian Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League of Legends champions and the power that will tear them apart.",
            backdropImageUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
            status: "Continuing",
            year: "2024",
            language: "EN",
            rating: 4.8,
            progress: 0
        )
        .background(Color.background)
        .assertSnapshot(layout: .defaultDevice, testName: "HeaderView")
    }
}
