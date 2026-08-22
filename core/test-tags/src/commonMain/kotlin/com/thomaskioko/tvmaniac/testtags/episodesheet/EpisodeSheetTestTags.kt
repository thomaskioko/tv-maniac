package com.thomaskioko.tvmaniac.testtags.episodesheet

public object EpisodeSheetTestTags {
    public const val SHEET_TEST_TAG: String = "episode_sheet"
    public const val TITLE_TEST_TAG: String = "episode_sheet_title"
    public const val PLAY_COUNT_TEST_TAG: String = "episode_sheet_play_count"
    public const val REMOVE_WATCH_CONFIRM_TEST_TAG: String = "episode_sheet_remove_watch_confirm"
    public const val REMOVE_WATCH_DISMISS_TEST_TAG: String = "episode_sheet_remove_watch_dismiss"
    public fun actionItem(name: String): String = "episode_sheet_action_${name.lowercase()}"
}
