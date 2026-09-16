package com.thomaskioko.tvmaniac.testtags.search

public object SearchTestTags {
    public const val SCREEN_TEST_TAG: String = "search_screen"
    public const val SEARCH_BAR_TEST_TAG: String = "search_bar"
    public const val EMPTY_STATE_TEST_TAG: String = "search_empty_state"
    public const val ERROR_STATE_TEST_TAG: String = "search_error_state"
    public const val RESULT_ITEM_TEST_TAG: String = "search_result_item"
    public const val RECENT_SEARCHES_SECTION_TEST_TAG: String = "search_recent_searches_section"
    public const val CLEAR_RECENT_SEARCHES_TEST_TAG: String = "search_clear_recent_searches"
    public fun resultItem(traktId: Long): String = "search_result_item_$traktId"
    public fun recentSearchChip(query: String): String = "search_recent_search_chip_$query"
}
