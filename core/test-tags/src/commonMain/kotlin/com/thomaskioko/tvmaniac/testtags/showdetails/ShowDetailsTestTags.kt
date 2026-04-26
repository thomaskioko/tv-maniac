package com.thomaskioko.tvmaniac.testtags.showdetails

public object ShowDetailsTestTags {
    public const val SHOW_DETAILS_SCREEN_TEST_TAG: String = "show_details_screen"
    public const val SHOW_DETAILS_TITLE_TEST_TAG: String = "show_details_title"
    public const val TRACK_BUTTON_TEST_TAG: String = "show_details_track_button"
    public const val STOP_TRACKING_BUTTON_TEST_TAG: String = "show_details_stop_tracking_button"
    public const val BACK_BUTTON_TEST_TAG: String = "show_details_back_button"
    public fun seasonChip(seasonNumber: Long): String = "show_details_season_chip_$seasonNumber"
}
