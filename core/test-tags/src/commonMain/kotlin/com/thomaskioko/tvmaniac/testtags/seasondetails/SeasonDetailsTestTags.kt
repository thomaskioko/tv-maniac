package com.thomaskioko.tvmaniac.testtags.seasondetails

public object SeasonDetailsTestTags {
    public const val SCREEN_TEST_TAG: String = "season_details_screen"
    public const val BACK_BUTTON_TEST_TAG: String = "season_details_back_button"
    public const val EPISODE_HEADER_TEST_TAG: String = "season_details_episode_header"
    public fun episodeRow(episodeId: Long): String = "season_details_episode_row_$episodeId"
    public fun markEpisodeWatchedButton(episodeId: Long): String =
        "season_details_episode_mark_watched_$episodeId"
    public fun markEpisodeUnwatchedButton(episodeId: Long): String =
        "season_details_episode_mark_unwatched_$episodeId"
}
