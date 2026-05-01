package com.thomaskioko.tvmaniac.testtags.upnext

public object UpNextTestTags {
    public const val EMPTY_STATE_TEST_TAG: String = "upnext_empty_state"
    public const val LIST_TEST_TAG: String = "upnext_list"
    public fun episodeRow(showTraktId: Long): String = "upnext_episode_row_$showTraktId"
    public fun watchedButton(showTraktId: Long): String = "upnext_watched_button_$showTraktId"
    public fun episodeName(showTraktId: Long, name: String): String = "upnext_episode_name_${showTraktId}_$name"
    public fun episodeMeta(showTraktId: Long, formattedEpisodeNumber: String): String =
        "upnext_episode_meta_${showTraktId}_$formattedEpisodeNumber"
    public fun progressCount(showTraktId: Long, count: String): String = "upnext_progress_count_${showTraktId}_$count"
}
