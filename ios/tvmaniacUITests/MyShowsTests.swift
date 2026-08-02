import TvManiacTestTags
import XCTest

final class MyShowsTests: XCTestCase {
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    func test_MyShows_ShowsBothPagesAndSortOptions() {
        let app = XCUIApplication.launchTvManiac()
        app.openTab(.myShows)

        XCTAssertTrue(
            app.element(MyShowsTestTags.shared.EMPTY_STATE_TEST_TAG).waitForExistence(timeout: UITestTimeouts.screen),
            "My Shows never showed its empty state for a signed out account."
        )

        app.buttons[MyShowsTestTags.shared.START_WATCHING_TAB].tap()
        XCTAssertTrue(
            app.element(StartWatchingTestTags.shared.EMPTY_STATE).waitForExistence(timeout: UITestTimeouts.screen),
            "Start Watching never showed its empty state for a signed out account."
        )

        app.buttons[MyShowsTestTags.shared.CONTINUE_WATCHING_TAB].tap()
        app.buttons[MyShowsTestTags.shared.SORT_BUTTON_TEST_TAG].tap()
        XCTAssertTrue(
            app.element(MyShowsTestTags.shared.SORT_SHEET_TEST_TAG).waitForExistence(timeout: UITestTimeouts.screen),
            "Tapping sort never opened the sort options."
        )
    }

    func test_MyShows_OpensLayoutMenuAndSelectsFreeLayout() {
        let app = XCUIApplication.launchTvManiac()
        app.openTab(.myShows)

        let menuButton = app.buttons[MyShowsTestTags.shared.LAYOUT_MENU_BUTTON_TEST_TAG]
        XCTAssertTrue(
            menuButton.waitForExistence(timeout: UITestTimeouts.screen),
            "My Shows never showed the layout menu button."
        )
        menuButton.tap()

        let gridOption = app.buttons[MyShowsTestTags.shared.LAYOUT_MENU_ITEM_GRID_TEST_TAG]
        XCTAssertTrue(
            gridOption.waitForExistence(timeout: UITestTimeouts.screen),
            "Opening the layout menu never showed the Grid option."
        )
        XCTAssertTrue(gridOption.isSelected, "Grid should be the default active layout.")

        let listOption = app.buttons[MyShowsTestTags.shared.LAYOUT_MENU_ITEM_LIST_TEST_TAG]
        XCTAssertTrue(listOption.exists, "The layout menu never showed the List option.")
        XCTAssertTrue(
            app.buttons[MyShowsTestTags.shared.LAYOUT_MENU_ITEM_COMPACT_TEST_TAG].exists,
            "The layout menu never showed the Compact option."
        )
        XCTAssertTrue(
            app.buttons[MyShowsTestTags.shared.LAYOUT_MENU_ITEM_DETAILED_TEST_TAG].exists,
            "The layout menu never showed the Detailed option."
        )
        listOption.tap()

        menuButton.tap()
        let reopenedListOption = app.buttons[MyShowsTestTags.shared.LAYOUT_MENU_ITEM_LIST_TEST_TAG]
        XCTAssertTrue(
            reopenedListOption.waitForExistence(timeout: UITestTimeouts.screen),
            "Re-opening the layout menu never showed the List option."
        )
        XCTAssertTrue(reopenedListOption.isSelected, "Selecting List never applied the layout.")
    }
}
