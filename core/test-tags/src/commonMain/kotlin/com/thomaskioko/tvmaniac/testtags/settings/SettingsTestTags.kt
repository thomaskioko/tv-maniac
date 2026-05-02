package com.thomaskioko.tvmaniac.testtags.settings

public object SettingsTestTags {
    public const val SCREEN_TEST_TAG: String = "settings_screen"
    public const val LIST_TEST_TAG: String = "settings_list"
    public const val BACK_BUTTON_TEST_TAG: String = "settings_back_button"
    public const val TRAKT_ACCOUNT_ROW_TEST_TAG: String = "settings_trakt_account_row"
    public const val LOGOUT_DIALOG_CONFIRM_BUTTON_TEST_TAG: String = "settings_logout_dialog_confirm"
    public const val LOGOUT_DIALOG_DISMISS_BUTTON_TEST_TAG: String = "settings_logout_dialog_dismiss"
    public const val EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG: String = "settings_episode_notifications_toggle"
    public fun imageQualityChip(name: String): String = "settings_image_quality_${name.lowercase()}"
    public fun themeSwatch(name: String): String = "settings_theme_swatch_${name.lowercase()}"
}
