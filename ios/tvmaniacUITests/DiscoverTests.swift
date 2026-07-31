import XCTest

final class DiscoverTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Discover_ShowsCardsFromTheSavedResponses() {
        let app = XCUIApplication.launchTvManiac()
        app.awaitScreen(TestTags.discoverScreen)

        for row in [DiscoverRow.trending, .upcoming] {
            let card = app.element(TestTags.discoverShowCard(row: row, showId: FixtureData.breakingBadId))
            XCTAssertTrue(
                card.waitForExistence(timeout: UITestTimeouts.screen),
                "The \(row.rawValue) row never showed the show from the saved responses."
            )
        }
    }
}
