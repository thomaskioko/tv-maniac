package com.thomaskioko.tvmaniac.testtags.watchlist

public object WatchlistTestTags {
    public const val SCREEN_TEST_TAG: String = "watchlist_screen"
    public const val WATCHLIST_GRID_TEST_TAG: String = "watchlist_grid"
    public const val WATCHLIST_LIST_TEST_TAG: String = "watchlist_list"
    public const val EMPTY_STATE_TEST_TAG: String = "watchlist_empty_state"
    public const val SEARCH_BUTTON_TEST_TAG: String = "watchlist_search_button"
    public const val SEARCH_BAR_TEST_TAG: String = "watchlist_search_bar"
    public const val TOGGLE_LIST_STYLE_BUTTON_TEST_TAG: String = "watchlist_toggle_list_style"
    public const val SORT_BUTTON_TEST_TAG: String = "watchlist_sort_button"
    public const val SORT_SHEET_TEST_TAG: String = "watchlist_sort_sheet"
    public fun showCard(traktId: Long): String = "watchlist_show_card_$traktId"
}
