package com.thomaskioko.tvmaniac.testtags.showdetails

public object ShowDetailsTestTags {
    public const val SHOW_DETAILS_SCREEN_TEST_TAG: String = "show_details_screen"
    public const val SHOW_DETAILS_TITLE_TEST_TAG: String = "show_details_title"
    public const val TRACK_BUTTON_TEST_TAG: String = "show_details_track_button"
    public const val STOP_TRACKING_BUTTON_TEST_TAG: String = "show_details_stop_tracking_button"
    public const val ADD_TO_LIST_BUTTON_TEST_TAG: String = "show_details_add_to_list_button"
    public const val LOGIN_REQUIRED_DIALOG_CONFIRM_BUTTON_TEST_TAG: String = "show_details_login_required_confirm"
    public const val BACK_BUTTON_TEST_TAG: String = "show_details_back_button"
    public const val REFRESH_BUTTON_TEST_TAG: String = "show_details_refresh_button"
    public const val ERROR_RETRY_BUTTON_TEST_TAG: String = "show_details_error_retry_button"
    public const val CAST_LIST_TEST_TAG: String = "show_details_cast_list"
    public const val TRAILERS_LIST_TEST_TAG: String = "show_details_trailers_list"
    public const val SIMILAR_SHOWS_LIST_TEST_TAG: String = "show_details_similar_shows_list"
    public const val CONTINUE_TRACKING_SECTION_TEST_TAG: String = "show_details_continue_tracking_section"
    public const val LIST_SHEET_TEST_TAG: String = "show_details_list_sheet"
    public const val LIST_SHEET_CLOSE_BUTTON_TEST_TAG: String = "show_details_list_sheet_close_button"
    public const val LIST_SHEET_CREATE_LIST_BUTTON_TEST_TAG: String = "show_details_list_sheet_create_button"
    public const val LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG: String = "show_details_list_sheet_create_input"
    public const val LIST_SHEET_CREATE_LIST_SUBMIT_TEST_TAG: String = "show_details_list_sheet_create_submit"
    public const val LIST_SHEET_CREATE_LIST_PROGRESS_TEST_TAG: String = "show_details_list_sheet_create_progress"
    public fun seasonChip(seasonNumber: Long): String = "show_details_season_chip_$seasonNumber"
    public fun continueTrackingMarkWatchedButton(episodeId: Long): String =
        "show_details_continue_tracking_mark_watched_$episodeId"
    public fun traktListItem(listId: Long): String = "show_details_trakt_list_$listId"
    public fun traktListItemSwitch(listId: Long): String = "show_details_trakt_list_switch_$listId"
    public fun traktListItemShowCount(listId: Long): String = "show_details_trakt_list_show_count_$listId"
}
