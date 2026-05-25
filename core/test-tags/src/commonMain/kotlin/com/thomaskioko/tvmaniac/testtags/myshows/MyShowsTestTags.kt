package com.thomaskioko.tvmaniac.testtags.myshows

public object MyShowsTestTags {
    public const val SCREEN_TEST_TAG: String = "my_shows_screen"
    public const val MY_SHOWS_GRID_TEST_TAG: String = "my_shows_grid"
    public const val MY_SHOWS_LIST_TEST_TAG: String = "my_shows_list"
    public const val EMPTY_STATE_TEST_TAG: String = "my_shows_empty_state"
    public const val SEARCH_BUTTON_TEST_TAG: String = "my_shows_search_button"
    public const val SEARCH_BAR_TEST_TAG: String = "my_shows_search_bar"
    public const val TOGGLE_LIST_STYLE_BUTTON_TEST_TAG: String = "my_shows_toggle_list_style"
    public const val SORT_BUTTON_TEST_TAG: String = "my_shows_sort_button"
    public const val SORT_SHEET_TEST_TAG: String = "my_shows_sort_sheet"
    public fun showCard(traktId: Long): String = "my_shows_show_card_$traktId"
}
