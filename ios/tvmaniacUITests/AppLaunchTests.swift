import TvManiacTestTags
import XCTest

final class AppLaunchTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_EachTab_ShowsItsScreenAfterColdLaunch() {
        let app = XCUIApplication.launchTvManiac()

        XCTAssertTrue(
            app.tabBar.waitForExistence(timeout: UITestTimeouts.launch),
            "Tab bar never appeared, so the app did not finish launching."
        )
        XCTAssertEqual(app.tabBar.buttons.count, TabIndex.allCases.count)

        app.awaitScreen(DiscoverTestTags.shared.SCREEN_TEST_TAG)

        for tab in TabIndex.allCases {
            app.tabBar.buttons.element(boundBy: tab.rawValue).tap()
            app.awaitScreen(tab.screenTag)
        }
    }
}

enum TabIndex: Int, CaseIterable {
    case discover
    case progress
    case myShows
    case profile

    var screenTag: String {
        switch self {
        case .discover: DiscoverTestTags.shared.SCREEN_TEST_TAG
        case .progress: ProgressTestTags.shared.SCREEN_TEST_TAG
        case .myShows: MyShowsTestTags.shared.SCREEN_TEST_TAG
        case .profile: ProfileTestTags.shared.SCREEN_TEST_TAG
        }
    }
}
