package com.thomaskioko.tvmaniac.testtags.discover

public object DiscoverTestTags {
    public const val SCREEN_TEST_TAG: String = "discover_screen"
    public const val DISCOVER_LIST_TEST_TAG: String = "discover_list"
    public const val SEARCH_BUTTON_TEST_TAG: String = "discover_search_button"
    public const val ERROR_RETRY_BUTTON_TEST_TAG: String = "discover_error_retry_button"
    public const val FEATURED_PAGER_TEST_TAG: String = "discover_featured_pager"
    public const val UP_NEXT_SECTION_TEST_TAG: String = "up_next_section"
    public const val PROGRESS_INDICATOR: String = "discover_progress_indicator"

    public const val ROW_KEY_TRENDING: String = "trending"
    public const val ROW_KEY_UPCOMING: String = "upcoming"
    public const val ROW_KEY_POPULAR: String = "popular"
    public const val ROW_KEY_TOP_RATED: String = "top_rated"

    public fun moreButton(rowKey: String): String = "discover_more_button_$rowKey"
    public fun showCard(rowKey: String, traktId: Long): String = "discover_show_card_${rowKey}_$traktId"
    public fun upNextCard(showTraktId: Long): String = "discover_up_next_card_$showTraktId"
    public fun featuredShowItem(traktId: Long): String = "discover_featured_show_$traktId"
}
