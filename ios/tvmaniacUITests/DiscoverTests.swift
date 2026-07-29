import XCTest

final class DiscoverTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Discover_ShowsTrendingCardFromTheSavedResponses() {
        assertCard(inRow: .trending)
    }

    func test_Discover_ShowsUpcomingCardFromTheSavedResponses() {
        assertCard(inRow: .upcoming)
    }

    private func assertCard(
        inRow row: DiscoverRow,
        file: StaticString = #filePath,
        line: UInt = #line
    ) {
        let app = XCUIApplication.launchTvManiac()
        app.awaitScreen(TestTags.discoverScreen)

        let card = app.element(TestTags.discoverShowCard(row: row, showId: FixtureData.breakingBadId))
        XCTAssertTrue(
            card.waitForExistence(timeout: UITestTimeouts.screen),
            "The \(row.rawValue) row never showed the show from the saved responses.",
            file: file,
            line: line
        )
    }
}
