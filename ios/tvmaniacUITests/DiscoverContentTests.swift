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
    /// First entry of `trakt/shows/favorite/success.json`, rendered by the featured section.
    static let featuredShowTitle = "Breaking Bad"
}
