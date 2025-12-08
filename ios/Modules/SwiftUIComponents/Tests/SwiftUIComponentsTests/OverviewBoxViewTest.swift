import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class OverviewBoxViewTest: SnapshotTestCase {
    func test_overviewBoxView_collapsed() {
        OverviewBoxView(
            overview: "Set in the utopian region of Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League champions-and the power that will tear them apart. Set in the utopian region of Piltover and the oppressed underground."
        )
        .padding()
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "OverviewBoxView_Collapsed")
    }

    func test_overviewBoxView_expaned() {
        OverviewBoxView(
            overview: "Set in the utopian region of Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League champions-and the power that will tear them apart. Set in the utopian region of Piltover and the oppressed underground.",
            showFullText: true,
            isTruncated: false
        )
        .padding()
        .themedPreview()
        .assertSnapshot(layout: .defaultDevice, testName: "OverviewBoxView_Expanded")
    }
}
