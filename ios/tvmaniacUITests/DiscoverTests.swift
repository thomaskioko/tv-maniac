import TvManiacTestTags
import XCTest

final class DiscoverTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Discover_ShowsCardsFromTheSavedResponses() {
        let app = XCUIApplication.launchTvManiac()
        app.awaitScreen(DiscoverTestTags.shared.SCREEN_TEST_TAG)

        for row in [DiscoverRow.trending, .upcoming] {
            let card = app.element(DiscoverTestTags.shared.showCard(
                rowKey: row.key,
                traktId: FixtureData.breakingBadId
            ))
            XCTAssertTrue(
                card.waitForExistence(timeout: UITestTimeouts.screen),
                "The \(row.key) row never showed the show from the saved responses."
            )
        }
    }
}
