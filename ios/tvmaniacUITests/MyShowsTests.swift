import XCTest

final class MyShowsTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_MyShows_ShowsEmptyStateWhenSignedOut() {
        let app = XCUIApplication.launchTvManiac()
        app.openTab(.myShows)

        let emptyState = app.element(TestTags.myShowsEmptyState)
        XCTAssertTrue(
            emptyState.waitForExistence(timeout: UITestTimeouts.screen),
            "My Shows never showed its empty state for a signed out account."
        )
    }

    func test_MyShows_SwitchesToStartWatching() {
        let app = XCUIApplication.launchTvManiac()
        app.openTab(.myShows)

        app.buttons[TestTags.myShowsStartWatchingTab].tap()

        let emptyState = app.element(TestTags.startWatchingEmptyState)
        XCTAssertTrue(
            emptyState.waitForExistence(timeout: UITestTimeouts.screen),
            "Start Watching never showed its empty state for a signed out account."
        )
    }

    func test_MyShows_OpensSortOptions() {
        let app = XCUIApplication.launchTvManiac()
        app.openTab(.myShows)

        app.buttons[TestTags.myShowsSortButton].tap()

        let sheet = app.element(TestTags.myShowsSortSheet)
        XCTAssertTrue(
            sheet.waitForExistence(timeout: UITestTimeouts.screen),
            "Tapping sort never opened the sort options."
        )
    }
}
