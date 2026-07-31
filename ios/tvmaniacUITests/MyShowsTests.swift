import TvManiacTestTags
import XCTest

final class MyShowsTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_MyShows_ShowsBothPagesAndSortOptions() {
        let app = XCUIApplication.launchTvManiac()
        app.openTab(.myShows)

        XCTAssertTrue(
            app.element(MyShowsTestTags.shared.EMPTY_STATE_TEST_TAG).waitForExistence(timeout: UITestTimeouts.screen),
            "My Shows never showed its empty state for a signed out account."
        )

        app.buttons[MyShowsTestTags.shared.START_WATCHING_TAB].tap()
        XCTAssertTrue(
            app.element(StartWatchingTestTags.shared.EMPTY_STATE).waitForExistence(timeout: UITestTimeouts.screen),
            "Start Watching never showed its empty state for a signed out account."
        )

        app.buttons[MyShowsTestTags.shared.CONTINUE_WATCHING_TAB].tap()
        app.buttons[MyShowsTestTags.shared.SORT_BUTTON_TEST_TAG].tap()
        XCTAssertTrue(
            app.element(MyShowsTestTags.shared.SORT_SHEET_TEST_TAG).waitForExistence(timeout: UITestTimeouts.screen),
            "Tapping sort never opened the sort options."
        )
    }
}
