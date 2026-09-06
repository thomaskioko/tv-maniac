package com.thomaskioko.tvmaniac.testtags.lists

public object ListsTestTags {
    public const val SCREEN_TEST_TAG: String = "lists_screen"
    public const val BACK_BUTTON_TEST_TAG: String = "lists_back_button"

    public fun listCard(id: Long): String = "lists_list_card_$id"
}
