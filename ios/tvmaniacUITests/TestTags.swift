import TvManiacTestTags

enum TestTags {
    static let discoverScreen = DiscoverTestTags.shared.SCREEN_TEST_TAG
    static let progressScreen = ProgressTestTags.shared.SCREEN_TEST_TAG
    static let myShowsScreen = MyShowsTestTags.shared.SCREEN_TEST_TAG
    static let profileScreen = ProfileTestTags.shared.SCREEN_TEST_TAG
    static let settingsScreen = SettingsTestTags.shared.SCREEN_TEST_TAG

    static let profileSettingsButton = ProfileTestTags.shared.SETTINGS_BUTTON_TEST_TAG
    static let settingsBackButton = SettingsTestTags.shared.BACK_BUTTON_TEST_TAG
    static let settingsAppearanceRow = SettingsTestTags.shared.GENERAL_APPEARANCE_ROW_TEST_TAG
    static let settingsInfoRow = SettingsTestTags.shared.ABOUT_INFO_ROW_TEST_TAG
    static let settingsVersionText = SettingsTestTags.shared.INFO_VERSION_TEXT_TEST_TAG

    static func settingsImageQualityChip(_ quality: String) -> String {
        SettingsTestTags.shared.imageQualityChip(name: quality)
    }

    static func discoverShowCard(row: DiscoverRow, showId: Int) -> String {
        DiscoverTestTags.shared.showCard(rowKey: row.rawValue, traktId: Int64(showId))
    }

    static let searchScreen = SearchTestTags.shared.SCREEN_TEST_TAG
    static let searchBar = SearchTestTags.shared.SEARCH_BAR_TEST_TAG
    static let discoverSearchButton = DiscoverTestTags.shared.SEARCH_BUTTON_TEST_TAG

    static func searchResultItem(_ showId: Int) -> String {
        SearchTestTags.shared.resultItem(traktId: Int64(showId))
    }

    static let myShowsContinueWatchingTab = MyShowsTestTags.shared.CONTINUE_WATCHING_TAB
    static let myShowsStartWatchingTab = MyShowsTestTags.shared.START_WATCHING_TAB
    static let myShowsEmptyState = MyShowsTestTags.shared.EMPTY_STATE_TEST_TAG
    static let myShowsSortButton = MyShowsTestTags.shared.SORT_BUTTON_TEST_TAG
    static let myShowsSortSheet = MyShowsTestTags.shared.SORT_SHEET_TEST_TAG
    static let startWatchingEmptyState = StartWatchingTestTags.shared.EMPTY_STATE

    static let progressUpNextTab = ProgressTestTags.shared.UPNEXT_TAB
    static let progressCalendarTab = ProgressTestTags.shared.CALENDAR_TAB
    static let upNextEmptyState = UpNextTestTags.shared.EMPTY_STATE_TEST_TAG
    static let calendarScreen = CalendarTestTags.shared.SCREEN_TEST_TAG
    static let calendarLoggedOutState = CalendarTestTags.shared.LOGGED_OUT_STATE_TEST_TAG

    static let showDetailsScreen = ShowDetailsTestTags.shared.SHOW_DETAILS_SCREEN_TEST_TAG
    static let showDetailsBackButton = ShowDetailsTestTags.shared.BACK_BUTTON_TEST_TAG

    static func showDetailsSeasonChip(_ seasonNumber: Int) -> String {
        ShowDetailsTestTags.shared.seasonChip(seasonNumber: Int64(seasonNumber))
    }
}
