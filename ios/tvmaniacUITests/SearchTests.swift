import XCTest

final class SearchTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Search_ShowsResultForTheStubbedQuery() {
        let app = XCUIApplication.launchTvManiac()
        app.awaitScreen(TestTags.discoverScreen)

        app.buttons[TestTags.discoverSearchButton].tap()
        app.awaitScreen(TestTags.searchScreen)

        let field = app.textFields[TestTags.searchBar]
        XCTAssertTrue(field.waitForExistence(timeout: UITestTimeouts.screen))
        field.tap()
        field.typeText(FixtureData.searchQuery)

        let result = app.element(TestTags.searchResultItem(FixtureData.breakingBadId))
        XCTAssertTrue(
            result.waitForExistence(timeout: UITestTimeouts.screen),
            "Searching for \(FixtureData.searchQuery) never showed a result from the saved responses."
        )
    }
}
