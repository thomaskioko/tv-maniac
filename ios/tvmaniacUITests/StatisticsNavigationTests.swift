import TvManiacTestTags
import XCTest

final class StatisticsNavigationTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Statistics_ShowsWatchHistoryAndReturnsToProfile() {
        let app = XCUIApplication.launchTvManiac(scenario: StubScenario.authenticatedTrakt)
        app.openTab(.profile)

        let statisticsRow = app.element(ProfileTestTags.shared.STATISTICS_ROW_TEST_TAG)
        XCTAssertTrue(
            statisticsRow.waitForExistence(timeout: UITestTimeouts.screen),
            "Profile never showed its statistics row for a signed in account."
        )
        statisticsRow.tap()
        app.awaitScreen(StatisticsTestTags.shared.SCREEN_TEST_TAG)

        XCTAssertTrue(
            app.element(StatisticsTestTags.shared.CONTENT_TEST_TAG)
                .waitForExistence(timeout: UITestTimeouts.screen),
            "Statistics never showed its content for a signed in account with watch history."
        )
        XCTAssertTrue(
            app.element(StatisticsTestTags.shared.TILE_GRID_TEST_TAG).exists,
            "Statistics content never showed its tile grid."
        )

        app.buttons[StatisticsTestTags.shared.BACK_BUTTON_TEST_TAG].tap()
        app.awaitScreen(ProfileTestTags.shared.SCREEN_TEST_TAG)
    }
}
