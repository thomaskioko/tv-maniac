package com.thomaskioko.tvmaniac.testtags.discover

public object DiscoverTestTags {
    public const val SCREEN_TEST_TAG: String = "discover_screen"
    public const val DISCOVER_LIST_TEST_TAG: String = "discover_list"
    public const val SEARCH_BUTTON_TEST_TAG: String = "discover_search_button"
    public const val ERROR_RETRY_BUTTON_TEST_TAG: String = "discover_error_retry_button"
    public const val FEATURED_PAGER_TEST_TAG: String = "discover_featured_pager"
    public fun moreButton(category: String): String = "discover_more_button_$category"
    public fun showCard(traktId: Long): String = "discover_show_card_$traktId"
    public fun upNextCard(showTraktId: Long): String = "discover_up_next_card_$showTraktId"
    public fun featuredShowItem(traktId: Long): String = "discover_featured_show_$traktId"
}
