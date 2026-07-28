enum TestTags {
    static let discoverScreen = "discover_screen"
    static let progressScreen = "progress_screen"
    static let myShowsScreen = "my_shows_screen"
    static let profileScreen = "profile_screen"
    static let settingsScreen = "settings_screen"

    static let profileSettingsButton = "profile_settings_button"
    static let settingsBackButton = "settings_back_button"

    static let discoverFeaturedPager = "discover_featured_pager"

    static func discoverFeaturedItem(_ showId: Int) -> String {
        "discover_featured_show_\(showId)"
    }

    static func discoverTrendingCard(_ showId: Int) -> String {
        "discover_show_card_trending_\(showId)"
    }

    static let searchScreen = "search_screen"
    static let searchBar = "search_bar"
    static let discoverSearchButton = "discover_search_button"

    static func searchResultItem(_ showId: Int) -> String {
        "search_result_item_\(showId)"
    }

    static let showDetailsScreen = "show_details_screen"
    static let showDetailsBackButton = "show_details_back_button"

    static func showDetailsSeasonChip(_ seasonNumber: Int) -> String {
        "show_details_season_chip_\(seasonNumber)"
    }
}
