package com.thomaskioko.tvmaniac.testtags.showlist

public object ShowListTestTags {
    public const val SHEET_TEST_TAG: String = "show_list_sheet"
    public const val CLOSE_BUTTON_TEST_TAG: String = "show_list_close_button"
    public const val CREATE_LIST_BUTTON_TEST_TAG: String = "show_list_create_button"
    public const val CREATE_LIST_INPUT_TEST_TAG: String = "show_list_create_input"
    public const val CREATE_LIST_SUBMIT_TEST_TAG: String = "show_list_create_submit"
    public const val CREATE_LIST_PROGRESS_TEST_TAG: String = "show_list_create_progress"
    public const val LOADING_INDICATOR_TEST_TAG: String = "show_list_loading_indicator"
    public fun listItem(listId: Long): String = "show_list_item_$listId"
    public fun listItemSwitch(listId: Long): String = "show_list_item_switch_$listId"
    public fun listItemProgress(listId: Long): String = "show_list_item_progress_$listId"
    public fun listItemShowCount(listId: Long): String = "show_list_item_show_count_$listId"
}
