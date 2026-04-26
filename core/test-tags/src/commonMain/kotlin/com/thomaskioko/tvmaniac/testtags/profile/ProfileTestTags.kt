package com.thomaskioko.tvmaniac.testtags.profile

public object ProfileTestTags {
    public const val SCREEN_TEST_TAG: String = "profile_screen"
    public const val SIGN_IN_BUTTON_TEST_TAG: String = "profile_sign_in_button"
    public const val SETTINGS_BUTTON_TEST_TAG: String = "profile_settings_button"
    public fun userCard(slug: String): String = "profile_user_card_$slug"
}
