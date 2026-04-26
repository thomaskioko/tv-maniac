package com.thomaskioko.tvmaniac.testtags.upnext

public object UpNextTestTags {
    public const val EMPTY_STATE_TEST_TAG: String = "upnext_empty_state"
    public fun episodeRow(showTraktId: Long): String = "upnext_episode_row_$showTraktId"
    public fun watchedButton(showTraktId: Long): String = "upnext_watched_button_$showTraktId"
    public fun episodeName(showTraktId: Long, name: String): String = "upnext_episode_name_${showTraktId}_$name"
}
