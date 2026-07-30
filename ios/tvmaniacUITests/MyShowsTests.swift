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
            app.element(TestTags.myShowsEmptyState).waitForExistence(timeout: UITestTimeouts.screen),
            "My Shows never showed its empty state for a signed out account."
        )

        app.buttons[TestTags.myShowsStartWatchingTab].tap()
        XCTAssertTrue(
            app.element(TestTags.startWatchingEmptyState).waitForExistence(timeout: UITestTimeouts.screen),
            "Start Watching never showed its empty state for a signed out account."
        )

        app.buttons[TestTags.myShowsContinueWatchingTab].tap()
        app.buttons[TestTags.myShowsSortButton].tap()
        XCTAssertTrue(
            app.element(TestTags.myShowsSortSheet).waitForExistence(timeout: UITestTimeouts.screen),
            "Tapping sort never opened the sort options."
        )
    }
}
