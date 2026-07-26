import XCTest

final class SettingsNavigationTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Settings_OpensFromProfileAndReturns() {
        let app = XCUIApplication.launchTvManiac()
        XCTAssertTrue(app.tabBar.waitForExistence(timeout: UITestTimeouts.launch))

        app.tabBar.buttons.element(boundBy: TabIndex.profile.rawValue).tap()
        app.awaitScreen(TestTags.profileScreen)

        let settingsButton = app.buttons[TestTags.profileSettingsButton]
        XCTAssertTrue(settingsButton.waitForExistence(timeout: UITestTimeouts.screen))
        settingsButton.tap()

        app.awaitScreen(TestTags.settingsScreen)

        app.buttons[TestTags.settingsBackButton].tap()
        app.awaitScreen(TestTags.profileScreen)
    }
}
