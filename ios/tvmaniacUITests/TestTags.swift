/// Literals rather than the Kotlin `core:test-tags` constants: importing `TvManiac` links the
/// whole Kotlin/Native framework into the test bundle, which fails on missing sqlite3 symbols.
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
}
