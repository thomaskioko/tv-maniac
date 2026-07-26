import XCTest

final class AppLaunchTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_TabBar_ShownAfterColdLaunch() {
        let app = XCUIApplication.launchTvManiac()

        XCTAssertTrue(
            app.tabBar.waitForExistence(timeout: UITestTimeouts.launch),
            "Tab bar never appeared, so the app did not finish launching."
        )
        XCTAssertEqual(app.tabBar.buttons.count, TabIndex.allCases.count)
    }

    func test_EachTab_ShowsItsScreen() {
        let app = XCUIApplication.launchTvManiac()
        XCTAssertTrue(app.tabBar.waitForExistence(timeout: UITestTimeouts.launch))

        app.awaitScreen(TestTags.discoverScreen)

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
        case .discover: TestTags.discoverScreen
        case .progress: TestTags.progressScreen
        case .myShows: TestTags.myShowsScreen
        case .profile: TestTags.profileScreen
        }
    }
}
