import SnapshotTestingLib
import SwiftUI
import TvManiacKit
import XCTest

class EpisodeItemViewTest: XCTestCase {
    func test_EpisodeItemView() {
        EpisodeItemView(
            imageUrl: "https://image.tmdb.org/t/p/w780/fqldf2t8ztc9aiwn3k6mlX3tvRT.jpg",
            episodeTitle: "E01 • Glorious Purpose",
            episodeOverView: "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority."
        )
        .padding()
        .background(Color.background)
        .assertSnapshot(testName: "EpisodeItemView")
    }
}
