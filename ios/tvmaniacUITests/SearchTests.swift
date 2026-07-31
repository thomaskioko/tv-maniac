import TvManiacTestTags
import XCTest

final class SearchTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Search_ShowsResultForTheStubbedQuery() {
        let app = XCUIApplication.launchTvManiac(scenario: StubScenario.search)
        app.awaitScreen(DiscoverTestTags.shared.SCREEN_TEST_TAG)

        app.buttons[DiscoverTestTags.shared.SEARCH_BUTTON_TEST_TAG].tap()
        app.awaitScreen(SearchTestTags.shared.SCREEN_TEST_TAG)

        let field = app.textFields[SearchTestTags.shared.SEARCH_BAR_TEST_TAG]
        XCTAssertTrue(field.waitForExistence(timeout: UITestTimeouts.screen))
        field.tap()
        field.typeText(FixtureData.searchQuery)

        let result = app.element(SearchTestTags.shared.resultItem(traktId: FixtureData.breakingBadId))
        XCTAssertTrue(
            result.waitForExistence(timeout: UITestTimeouts.screen),
            "Searching for \(FixtureData.searchQuery) never showed a result from the saved responses."
        )
    }
}
