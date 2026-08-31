package com.thomaskioko.tvmaniac.testtags.widget

public object WidgetTestTags {
    public const val TITLE_TEST_TAG: String = "widget_title"
    public const val EMPTY_STATE_TEST_TAG: String = "widget_empty_state"
    public fun episodeRow(showId: Long): String = "widget_episode_row_$showId"
    public fun poster(showId: Long): String = "widget_poster_$showId"
}
