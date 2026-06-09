import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class BadgeChipTest: SnapshotTestCase {
    func test_PremiereBadge() {
        PremiereBadge(text: "Premiere")
            .padding()
            .appPreview()
            .assertSnapshot(testName: "PremiereBadge")
    }

    func test_NewBadge() {
        NewBadge(text: "New")
            .padding()
            .appPreview()
            .assertSnapshot(testName: "NewBadge")
    }
}
