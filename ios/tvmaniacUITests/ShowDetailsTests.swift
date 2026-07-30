import XCTest

final class ShowDetailsTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_ShowDetails_ShowsSeasonsAndGoesBackToSearch() {
        let app = XCUIApplication.launchTvManiac(scenario: StubScenario.search)
        app.openShowDetailsFromSearch()
        app.awaitScreen(TestTags.showDetailsScreen)

        XCTAssertTrue(
            app.element(TestTags.showDetailsSeasonChip(1)).waitForExistence(timeout: UITestTimeouts.screen),
            "Show details never listed season 1. The app is not reading the saved responses."
        )

        let backButton = app.element(TestTags.showDetailsBackButton)
        XCTAssertTrue(
            backButton.waitForExistence(timeout: UITestTimeouts.screen),
            "Show details never showed its back button."
        )
        backButton.tap()

        app.awaitScreen(TestTags.searchScreen)
    }
}
