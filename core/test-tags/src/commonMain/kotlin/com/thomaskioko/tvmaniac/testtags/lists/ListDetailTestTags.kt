package com.thomaskioko.tvmaniac.testtags.lists

public object ListDetailTestTags {
    public const val SCREEN_TEST_TAG: String = "list_detail_screen"
    public const val GRID_TEST_TAG: String = "list_detail_grid"
    public const val BACK_BUTTON_TEST_TAG: String = "list_detail_back_button"
    public const val REMOVE_CONFIRM_BUTTON_TEST_TAG: String = "list_detail_remove_confirm_button"
    public const val DISMISS_ERROR_BUTTON_TEST_TAG: String = "list_detail_dismiss_error"
    public const val REMOVE_CANCEL_BUTTON_TEST_TAG: String = "list_detail_remove_cancel_button"

    public fun showCard(tmdbId: Long): String = "list_detail_show_card_$tmdbId"
}
