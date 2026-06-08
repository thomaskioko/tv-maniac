import DesignSystem
import XCTest

open class SnapshotTestCase: XCTestCase {
    override open func setUp() {
        super.setUp()
        TvManiacTypographyScheme.configureForTesting()
    }
}
