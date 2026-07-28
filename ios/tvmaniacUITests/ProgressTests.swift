import XCTest

final class ProgressTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Progress_ShowsUpNextEmptyStateWhenSignedOut() {
        let app = XCUIApplication.launchTvManiac()
        app.openProgress()

        let emptyState = app.element(TestTags.upNextEmptyState)
        XCTAssertTrue(
            emptyState.waitForExistence(timeout: UITestTimeouts.screen),
            "Up Next never showed its empty state for a signed out account."
        )
    }

    func test_Progress_SwitchesToCalendar() {
        let app = XCUIApplication.launchTvManiac()
        app.openProgress()

        app.buttons[TestTags.progressCalendarTab].tap()
        app.awaitScreen(TestTags.calendarScreen)

        let loggedOutState = app.element(TestTags.calendarLoggedOutState)
        XCTAssertTrue(
            loggedOutState.waitForExistence(timeout: UITestTimeouts.screen),
            "The calendar never asked a signed out account to log in."
        )
    }
}
