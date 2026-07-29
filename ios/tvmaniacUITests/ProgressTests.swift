import XCTest

final class ProgressTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Progress_ShowsBothPagesWhenSignedOut() {
        let app = XCUIApplication.launchTvManiac()
        app.openTab(.progress)

        XCTAssertTrue(
            app.element(TestTags.upNextEmptyState).waitForExistence(timeout: UITestTimeouts.screen),
            "Up Next never showed its empty state for a signed out account."
        )

        app.buttons[TestTags.progressCalendarTab].tap()
        app.awaitScreen(TestTags.calendarScreen)

        XCTAssertTrue(
            app.element(TestTags.calendarLoggedOutState).waitForExistence(timeout: UITestTimeouts.screen),
            "The calendar never asked a signed out account to log in."
        )
    }
}
