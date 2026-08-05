package com.thomaskioko.tvmaniac.testtags.statistics

public object StatisticsTestTags {
    public const val SCREEN_TEST_TAG: String = "statistics_screen"
    public const val BACK_BUTTON_TEST_TAG: String = "statistics_back_button"
    public const val LOADING_INDICATOR_TEST_TAG: String = "statistics_loading_indicator"
    public const val EMPTY_STATE_TEST_TAG: String = "statistics_empty_state"
    public const val LOCKED_STATE_TEST_TAG: String = "statistics_locked_state"
    public const val CONTENT_TEST_TAG: String = "statistics_content"
    public const val WATCH_TIME_HERO_TEST_TAG: String = "statistics_watch_time_hero"
    public const val TILE_GRID_TEST_TAG: String = "statistics_tile_grid"
    public const val MARKED_WATCHED_NOTE_TEST_TAG: String = "statistics_marked_watched_note"
    public const val MOST_WATCHED_ROW_TEST_TAG: String = "statistics_most_watched_row"
    public const val WATCH_STATUS_SECTION_TEST_TAG: String = "statistics_watch_status_section"
    public const val RATINGS_SECTION_TEST_TAG: String = "statistics_ratings_section"
    public fun tile(id: String): String = "statistics_tile_$id"
    public fun mostWatchedShowCard(showId: Long): String = "statistics_most_watched_show_$showId"
    public fun watchStatusRow(id: String): String = "statistics_watch_status_row_$id"
    public fun ratingRow(rating: Int): String = "statistics_rating_row_$rating"
}
