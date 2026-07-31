import TvManiacTestTags
import XCTest

final class SettingsNavigationTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_Settings_WalksItsPagesAndReturnsToProfile() {
        let app = XCUIApplication.launchTvManiac()
        app.openTab(.profile)

        let settingsButton = app.buttons[ProfileTestTags.shared.SETTINGS_BUTTON_TEST_TAG]
        XCTAssertTrue(
            settingsButton.waitForExistence(timeout: UITestTimeouts.screen),
            "Profile never showed its settings button."
        )
        settingsButton.tap()
        app.awaitScreen(SettingsTestTags.shared.SCREEN_TEST_TAG)

        app.openSettingsPage(SettingsTestTags.shared.GENERAL_APPEARANCE_ROW_TEST_TAG)
        XCTAssertTrue(
            app.element(SettingsTestTags.shared.imageQualityChip(name: "AUTO"))
                .waitForExistence(timeout: UITestTimeouts.screen),
            "The appearance page never showed the image quality choices."
        )
        app.leaveSettingsPage()

        app.openSettingsPage(SettingsTestTags.shared.ABOUT_INFO_ROW_TEST_TAG)
        XCTAssertTrue(
            app.element(SettingsTestTags.shared.INFO_VERSION_TEXT_TEST_TAG)
                .waitForExistence(timeout: UITestTimeouts.screen),
            "The about page never showed the version."
        )
        app.leaveSettingsPage()

        app.buttons[SettingsTestTags.shared.BACK_BUTTON_TEST_TAG].tap()
        app.awaitScreen(ProfileTestTags.shared.SCREEN_TEST_TAG)
    }
}
