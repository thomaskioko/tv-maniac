package com.thomaskioko.tvmaniac.testtags.showlist

public object ShowListTestTags {
    public const val SHEET_TEST_TAG: String = "show_list_sheet"
    public const val CLOSE_BUTTON_TEST_TAG: String = "show_list_close_button"
    public const val CREATE_LIST_BUTTON_TEST_TAG: String = "show_list_create_button"
    public const val CREATE_LIST_INPUT_TEST_TAG: String = "show_list_create_input"
    public const val CREATE_LIST_SUBMIT_TEST_TAG: String = "show_list_create_submit"
    public const val CREATE_LIST_PROGRESS_TEST_TAG: String = "show_list_create_progress"
    public const val LOGIN_REQUIRED_CONFIRM_BUTTON_TEST_TAG: String = "show_list_login_required_confirm"
    public fun traktListItem(listId: Long): String = "show_list_item_$listId"
    public fun traktListItemSwitch(listId: Long): String = "show_list_item_switch_$listId"
    public fun traktListItemShowCount(listId: Long): String = "show_list_item_show_count_$listId"
}
