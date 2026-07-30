import XCTest

final class SettingsNavigationTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Settings_WalksItsPagesAndReturnsToProfile() {
        let app = XCUIApplication.launchTvManiac()
        app.openTab(.profile)

        let settingsButton = app.buttons[TestTags.profileSettingsButton]
        XCTAssertTrue(
            settingsButton.waitForExistence(timeout: UITestTimeouts.screen),
            "Profile never showed its settings button."
        )
        settingsButton.tap()
        app.awaitScreen(TestTags.settingsScreen)

        app.openSettingsPage(TestTags.settingsAppearanceRow)
        XCTAssertTrue(
            app.element(TestTags.settingsImageQualityChip("AUTO")).waitForExistence(timeout: UITestTimeouts.screen),
            "The appearance page never showed the image quality choices."
        )
        app.leaveSettingsPage()

        app.openSettingsPage(TestTags.settingsInfoRow)
        XCTAssertTrue(
            app.element(TestTags.settingsVersionText).waitForExistence(timeout: UITestTimeouts.screen),
            "The about page never showed the version."
        )
        app.leaveSettingsPage()

        app.buttons[TestTags.settingsBackButton].tap()
        app.awaitScreen(TestTags.profileScreen)
    }
}
