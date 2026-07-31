import TvManiacTestTags
import XCTest

final class ShowDetailsTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_ShowDetails_ShowsSeasonsAndGoesBackToSearch() {
        let app = XCUIApplication.launchTvManiac(scenario: StubScenario.search)
        app.openShowDetailsFromSearch()
        app.awaitScreen(ShowDetailsTestTags.shared.SHOW_DETAILS_SCREEN_TEST_TAG)

        XCTAssertTrue(
            app.element(ShowDetailsTestTags.shared.seasonChip(seasonNumber: 1))
                .waitForExistence(timeout: UITestTimeouts.screen),
            "Show details never listed season 1. The app is not reading the saved responses."
        )

        let backButton = app.element(ShowDetailsTestTags.shared.BACK_BUTTON_TEST_TAG)
        XCTAssertTrue(
            backButton.waitForExistence(timeout: UITestTimeouts.screen),
            "Show details never showed its back button."
        )
        backButton.tap()

        app.awaitScreen(SearchTestTags.shared.SCREEN_TEST_TAG)
    }
}
