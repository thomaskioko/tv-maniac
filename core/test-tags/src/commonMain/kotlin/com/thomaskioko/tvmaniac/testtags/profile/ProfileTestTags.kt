package com.thomaskioko.tvmaniac.testtags.profile

public object ProfileTestTags {
    public const val SCREEN_TEST_TAG: String = "profile_screen"
    public const val SIGN_IN_BUTTON_TEST_TAG: String = "profile_sign_in_button"
    public const val SETTINGS_BUTTON_TEST_TAG: String = "profile_settings_button"
    public const val USERNAME_TEST_TAG: String = "profile_user_name"
    public const val USER_LISTS_ROW_TEST_TAG: String = "profile_user_lists_row"
    public const val USER_LISTS_RETRY_TEST_TAG: String = "profile_user_lists_retry"
    public const val USER_LISTS_SECTION_KEY: String = "profile_user_lists"
    public const val PROGRESS_SECTION_KEY: String = "profile_progress"
    public const val PROGRESS_ROW_TEST_TAG: String = "profile_progress_row"
    public const val PROGRESS_RETRY_TEST_TAG: String = "profile_progress_retry"
    public const val PROGRESS_COMPLETED_CHIP_TEST_TAG: String = "profile_progress_chip_completed"
    public const val PROGRESS_IN_PROGRESS_CHIP_TEST_TAG: String = "profile_progress_chip_in_progress"
    public const val RECENTLY_WATCHED_SECTION_KEY: String = "profile_recently_watched"
    public const val RECENTLY_WATCHED_ROW_TEST_TAG: String = "profile_recently_watched_row"
    public const val RECENTLY_WATCHED_RETRY_TEST_TAG: String = "profile_recently_watched_retry"
    public const val FAVORITES_SECTION_KEY: String = "profile_favorites"
    public const val FAVORITES_ROW_TEST_TAG: String = "profile_favorites_row"
    public const val FAVORITES_RETRY_TEST_TAG: String = "profile_favorites_retry"

    public fun userCard(slug: String): String = "profile_user_card_$slug"
    public fun listCard(id: Long): String = "profile_list_card_$id"
    public fun showCard(id: Long): String = "profile_show_card_$id"
}
