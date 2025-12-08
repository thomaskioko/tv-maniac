import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ChevronTitleTest: SnapshotTestCase {
    func test_ChevronTitle() {
        ChevronTitle(title: "Coming Soon")
            .padding()
            .themedPreview()
            .assertSnapshot(testName: "ChevronTitle")
    }

    func test_ChevronTitleWithChevronOnly() {
        ChevronTitle(title: "Coming Soon", chevronStyle: .chevronOnly)
            .padding()
            .themedPreview()
            .assertSnapshot(testName: "ChevronTitleWithChevronOnly")
    }

    func test_ChevronTitleWithChevronTitle() {
        ChevronTitle(title: "Coming Soon", chevronStyle: .withTitle("More"))
            .padding()
            .themedPreview()
            .assertSnapshot(testName: "ChevronTitleWithChevronTitle")
    }

    func test_ChevronTitleWithSubTitle() {
        ChevronTitle(title: "Coming Soon", subtitle: "From Watchlist")
            .padding()
            .themedPreview()
            .assertSnapshot(testName: "ChevronTitleWithSubTitle")
    }

    func test_ChevronTitleWithSubTitleAndChevronOnly() {
        ChevronTitle(title: "Coming Soon", subtitle: "From Watchlist", chevronStyle: .chevronOnly)
            .padding()
            .themedPreview()
            .assertSnapshot(testName: "ChevronTitleWithSubTitleAndChevronOnly")
    }
}
