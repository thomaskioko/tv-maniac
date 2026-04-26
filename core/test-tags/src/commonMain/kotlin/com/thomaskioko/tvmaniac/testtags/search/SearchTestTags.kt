package com.thomaskioko.tvmaniac.testtags.search

public object SearchTestTags {
    public const val SCREEN_TEST_TAG: String = "search_screen"
    public const val SEARCH_BAR_TEST_TAG: String = "search_bar"
    public const val EMPTY_STATE_TEST_TAG: String = "search_empty_state"
    public const val ERROR_STATE_TEST_TAG: String = "search_error_state"
    public const val RESULT_ITEM_TEST_TAG: String = "search_result_item"
    public fun resultItem(traktId: Long): String = "search_result_item_$traktId"
}
