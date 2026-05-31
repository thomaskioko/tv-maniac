import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ChevronTitleTest: SnapshotTestCase {
    func test_ChevronTitle() {
        ChevronTitle(title: "Coming Soon")
            .padding()
            .appPreview()
            .assertSnapshot(testName: "ChevronTitle")
    }

    func test_ChevronTitleWithChevronOnly() {
        ChevronTitle(title: "Coming Soon", chevronStyle: .chevronOnly)
            .padding()
            .appPreview()
            .assertSnapshot(testName: "ChevronTitleWithChevronOnly")
    }

    func test_ChevronTitleWithChevronTitle() {
        ChevronTitle(title: "Coming Soon", chevronStyle: .withTitle("More"))
            .padding()
            .appPreview()
            .assertSnapshot(testName: "ChevronTitleWithChevronTitle")
    }

    func test_ChevronTitleWithSubTitle() {
        ChevronTitle(title: "Coming Soon", subtitle: "From Watchlist")
            .padding()
            .appPreview()
            .assertSnapshot(testName: "ChevronTitleWithSubTitle")
    }

    func test_ChevronTitleWithSubTitleAndChevronOnly() {
        ChevronTitle(title: "Coming Soon", subtitle: "From Watchlist", chevronStyle: .chevronOnly)
            .padding()
            .appPreview()
            .assertSnapshot(testName: "ChevronTitleWithSubTitleAndChevronOnly")
    }
}
