import XCTest

final class DiscoverTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Discover_ShowsTrendingCardFromTheSavedResponses() {
        let app = XCUIApplication.launchTvManiac()
        app.awaitScreen(TestTags.discoverScreen)

        let card = app.element(TestTags.discoverTrendingCard(FixtureData.breakingBadId))
        XCTAssertTrue(
            card.waitForExistence(timeout: UITestTimeouts.screen),
            "Trending row never showed Breaking Bad. The app is not reading the saved responses."
        )
    }

    func test_FeaturedPager_ChangesShowOnSwipe() {
        let app = XCUIApplication.launchTvManiac()
        app.awaitScreen(TestTags.discoverScreen)

        let pager = app.scrollViews[TestTags.discoverFeaturedPager]
        XCTAssertTrue(pager.waitForExistence(timeout: UITestTimeouts.screen))

        let first = app.element(TestTags.discoverFeaturedItem(FixtureData.breakingBadId))
        XCTAssertTrue(first.waitForExistence(timeout: UITestTimeouts.screen))

        pager.swipeLeft()

        let second = app.element(TestTags.discoverFeaturedItem(FixtureData.betterCallSaulId))
        XCTAssertTrue(
            second.waitForExistence(timeout: UITestTimeouts.screen),
            "Swiping the featured pager did not reach the second featured show."
        )
    }
}
