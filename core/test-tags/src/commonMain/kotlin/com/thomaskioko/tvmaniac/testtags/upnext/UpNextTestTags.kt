package com.thomaskioko.tvmaniac.testtags.upnext

public object UpNextTestTags {
    public const val EMPTY_STATE_TEST_TAG: String = "upnext_empty_state"
    public const val EMPTY_STATE_LIST_TEST_TAG: String = "upnext_empty_state_list"
    public const val PAGE_LIST_TEST_TAG: String = "upnext_page_list"
    public const val SCREEN_TEST_TAG: String = "upnext_screen_list"
    public const val PROGRESS_INDICATOR: String = "upnext_progress_indicator"
    public fun episodeRow(showId: Long): String = "upnext_episode_row_$showId"
    public fun watchedButton(showId: Long): String = "upnext_watched_button_$showId"
}
