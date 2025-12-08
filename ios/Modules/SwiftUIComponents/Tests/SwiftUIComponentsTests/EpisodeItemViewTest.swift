import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class EpisodeItemViewTest: SnapshotTestCase {
    func test_EpisodeItemView() {
        EpisodeItemView(
            imageUrl: "",
            episodeTitle: "E01 • Glorious Purpose",
            episodeOverView: "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority."
        )
        .padding()
        .themedPreview()
        .assertSnapshot(testName: "EpisodeItemView")
    }
}
