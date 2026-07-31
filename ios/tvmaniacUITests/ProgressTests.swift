import TvManiacTestTags
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
            app.element(UpNextTestTags.shared.EMPTY_STATE_TEST_TAG).waitForExistence(timeout: UITestTimeouts.screen),
            "Up Next never showed its empty state for a signed out account."
        )

        app.buttons[ProgressTestTags.shared.CALENDAR_TAB].tap()
        app.awaitScreen(CalendarTestTags.shared.SCREEN_TEST_TAG)

        XCTAssertTrue(
            app.element(CalendarTestTags.shared.LOGGED_OUT_STATE_TEST_TAG)
                .waitForExistence(timeout: UITestTimeouts.screen),
            "The calendar never asked a signed out account to log in."
        )
    }
}
