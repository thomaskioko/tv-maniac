package com.thomaskioko.tvmaniac.testtags.seasondetails

public object SeasonDetailsTestTags {
    public const val SCREEN_TEST_TAG: String = "season_details_screen"
    public const val BACK_BUTTON_TEST_TAG: String = "season_details_back_button"
    public const val EPISODE_HEADER_TEST_TAG: String = "season_details_episode_header"
    public const val SEASON_WATCHED_TOGGLE_TEST_TAG: String = "season_details_season_watched_toggle"
    public const val MARK_PREVIOUS_EPISODES_DIALOG_CONFIRM_BUTTON_TEST_TAG: String =
        "season_details_mark_previous_episodes_confirm"
    public const val MARK_PREVIOUS_EPISODES_DIALOG_DISMISS_BUTTON_TEST_TAG: String =
        "season_details_mark_previous_episodes_dismiss"
    public const val MARK_PREVIOUS_SEASONS_DIALOG_CONFIRM_BUTTON_TEST_TAG: String =
        "season_details_mark_previous_seasons_confirm"
    public const val MARK_PREVIOUS_SEASONS_DIALOG_DISMISS_BUTTON_TEST_TAG: String =
        "season_details_mark_previous_seasons_dismiss"
    public const val UNWATCH_SEASON_DIALOG_CONFIRM_BUTTON_TEST_TAG: String =
        "season_details_unwatch_season_confirm"
    public const val UNWATCH_SEASON_DIALOG_DISMISS_BUTTON_TEST_TAG: String =
        "season_details_unwatch_season_dismiss"
    public const val UNWATCH_EPISODE_DIALOG_CONFIRM_BUTTON_TEST_TAG: String =
        "season_details_unwatch_episode_confirm"
    public const val UNWATCH_EPISODE_DIALOG_DISMISS_BUTTON_TEST_TAG: String =
        "season_details_unwatch_episode_dismiss"
    public fun episodeRow(episodeId: Long): String = "season_details_episode_row_$episodeId"
    public fun markEpisodeWatchedButton(episodeId: Long): String =
        "season_details_episode_mark_watched_$episodeId"
    public fun markEpisodeUnwatchedButton(episodeId: Long): String =
        "season_details_episode_mark_unwatched_$episodeId"
}
