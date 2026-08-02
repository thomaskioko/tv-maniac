package com.thomaskioko.tvmaniac.testtags.library

public object LibraryTestTags {
    public const val SCREEN_TEST_TAG: String = "library_screen"
    public const val LIBRARY_LIST_TEST_TAG: String = "library_list"
    public const val LIBRARY_COMPACT_LIST_TEST_TAG: String = "library_compact_list"
    public const val LIBRARY_DETAILED_LIST_TEST_TAG: String = "library_detailed_list"
    public const val EMPTY_STATE_TEST_TAG: String = "library_empty_state"
    public const val FILTER_BUTTON_TEST_TAG: String = "library_filter_button"
    public const val SEARCH_BUTTON_TEST_TAG: String = "library_search_button"
    public const val SEARCH_BAR_TEST_TAG: String = "library_search_bar"
    public const val APPLY_FILTER_BUTTON_TEST_TAG: String = "library_apply_filter_button"
    public const val CLEAR_FILTER_BUTTON_TEST_TAG: String = "library_clear_filter_button"
    public const val LAYOUT_MENU_BUTTON_TEST_TAG: String = "library_layout_menu_button"
    public const val LAYOUT_MENU_TEST_TAG: String = "library_layout_menu"
    public const val LAYOUT_MENU_LOCKED_SECTION_TEST_TAG: String = "library_layout_menu_locked_section"
    public const val LAYOUT_MENU_ITEM_GRID_TEST_TAG: String = "library_layout_menu_item_grid"
    public const val LAYOUT_MENU_ITEM_LIST_TEST_TAG: String = "library_layout_menu_item_list"
    public const val LAYOUT_MENU_ITEM_COMPACT_TEST_TAG: String = "library_layout_menu_item_compact"
    public const val LAYOUT_MENU_ITEM_DETAILED_TEST_TAG: String = "library_layout_menu_item_detailed"
    public fun showRow(traktId: Long): String = "library_show_row_$traktId"
}
