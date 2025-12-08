import SwiftUIComponents
import XCTest

class SnapshotTestCase: XCTestCase {
    override func setUp() {
        super.setUp()
        TvManiacTypographyScheme.configureForTesting()
    }
}
