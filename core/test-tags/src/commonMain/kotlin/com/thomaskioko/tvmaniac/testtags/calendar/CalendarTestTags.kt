package com.thomaskioko.tvmaniac.testtags.calendar

public object CalendarTestTags {
    public const val SCREEN_TEST_TAG: String = "calendar_screen"
    public const val EMPTY_STATE_TEST_TAG: String = "calendar_empty_state"
    public const val LOGGED_OUT_STATE_TEST_TAG: String = "calendar_logged_out_state"
    public const val PREVIOUS_WEEK_BUTTON: String = "calendar_previous_week_button"
    public const val NEXT_WEEK_BUTTON: String = "calendar_next_week_button"
    public const val WEEK_LABEL: String = "calendar_week_label"
    public fun episodeCard(episodeTraktId: Long): String = "calendar_episode_card_$episodeTraktId"
}
