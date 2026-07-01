package com.thomaskioko.tvmaniac.testtags.myshows

public object MyShowsTestTags {
    public const val SCREEN_TEST_TAG: String = "my_shows_screen"
    public const val TAB_ROW: String = "my_shows_tab_row"
    public const val HORIZONTAL_PAGER: String = "my_shows_horizontal_pager"
    public const val CONTINUE_WATCHING_TAB: String = "my_shows_tab_0"
    public const val START_WATCHING_TAB: String = "my_shows_tab_1"
    public const val MY_SHOWS_GRID_TEST_TAG: String = "my_shows_grid"
    public const val MY_SHOWS_LIST_TEST_TAG: String = "my_shows_list"
    public const val EMPTY_STATE_TEST_TAG: String = "my_shows_empty_state"
    public const val SEARCH_BUTTON_TEST_TAG: String = "my_shows_search_button"
    public const val SEARCH_BAR_TEST_TAG: String = "my_shows_search_bar"
    public const val TOGGLE_LIST_STYLE_BUTTON_TEST_TAG: String = "my_shows_toggle_list_style"
    public const val SORT_BUTTON_TEST_TAG: String = "my_shows_sort_button"
    public const val SORT_SHEET_TEST_TAG: String = "my_shows_sort_sheet"
    public const val MY_SHOWS_INDICATOR: String = "my_shows_loading_indicator"
    public fun showCard(traktId: Long): String = "my_shows_show_card_$traktId"
}
