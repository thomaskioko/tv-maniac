package com.thomaskioko.tvmaniac.testtags.upnext

public object UpNextTestTags {
    public const val EMPTY_STATE_TEST_TAG: String = "upnext_empty_state"
    public const val LIST_TEST_TAG: String = "upnext_list"
    public fun episodeRow(showTraktId: Long): String = "upnext_episode_row_$showTraktId"
    public fun watchedButton(showTraktId: Long): String = "upnext_watched_button_$showTraktId"
}
