import SnapshotTestingLib
import SwiftUI
import SwiftUIComponents
import XCTest

class ChevronTitleTest: XCTestCase {
    func test_ChevronTitle() {
        ChevronTitle(title: "Coming Soon")
            .padding()
            .background(Color.background)
            .assertSnapshot(testName: "ChevronTitle")
    }

    func test_ChevronTitleWithChevronOnly() {
        ChevronTitle(title: "Coming Soon", chevronStyle: .chevronOnly)
            .padding()
            .background(Color.background)
            .assertSnapshot(testName: "ChevronTitleWithChevronOnly")
    }

    func test_ChevronTitleWithChevronTitle() {
        ChevronTitle(title: "Coming Soon", chevronStyle: .withTitle("More"))
            .padding()
            .background(Color.background)
            .assertSnapshot(testName: "ChevronTitleWithChevronTitle")
    }

    func test_ChevronTitleWithSubTitle() {
        ChevronTitle(title: "Coming Soon", subtitle: "From Watchlist")
            .padding()
            .background(Color.background)
            .assertSnapshot(testName: "ChevronTitleWithSubTitle")
    }

    func test_ChevronTitleWithSubTitleAndChevronOnly() {
        ChevronTitle(title: "Coming Soon", subtitle: "From Watchlist", chevronStyle: .chevronOnly)
            .padding()
            .background(Color.background)
            .assertSnapshot(testName: "ChevronTitleWithSubTitleAndChevronOnly")
    }
}
