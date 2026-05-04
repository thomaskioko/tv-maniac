package com.thomaskioko.tvmaniac.testtags.episodesheet

public object EpisodeSheetTestTags {
    public const val SHEET_TEST_TAG: String = "episode_sheet"
    public const val TITLE_TEST_TAG: String = "episode_sheet_title"
    public fun actionItem(name: String): String = "episode_sheet_action_${name.lowercase()}"
}
