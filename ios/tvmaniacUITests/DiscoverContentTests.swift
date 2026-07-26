import XCTest

final class DiscoverContentTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Discover_ShowsAShowFromTheSavedResponses() {
        let app = XCUIApplication.launchTvManiac()
        XCTAssertTrue(app.tabBar.waitForExistence(timeout: UITestTimeouts.launch))
        app.awaitScreen(TestTags.discoverScreen)

        let title = app.staticTexts[FixtureData.featuredShowTitle]
        XCTAssertTrue(
            title.waitForExistence(timeout: UITestTimeouts.screen),
            "Discover never showed \"\(FixtureData.featuredShowTitle)\". "
                + "The app is not reading the saved responses."
        )
    }
}

enum FixtureData {
    /// The featured carousel holds these two in order, which is what the Android journey asserts.
    static let featuredShowTitle = "Breaking Bad"
    static let breakingBadId = 1396
    static let betterCallSaulId = 60059

    /// Matches `Scenarios.SEARCH_QUERY`, the only query the scenarios stub.
    static let searchQuery = "Breaking Bad"
}
