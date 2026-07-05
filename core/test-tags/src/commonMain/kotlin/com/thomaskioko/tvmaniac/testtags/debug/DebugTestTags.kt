package com.thomaskioko.tvmaniac.testtags.debug

public object DebugTestTags {
    public const val SCREEN_TEST_TAG: String = "debug_screen"
    public const val LIST_TEST_TAG: String = "debug_list"
    public const val ACCOUNT_TYPE_ROW_TEST_TAG: String = "debug_account_type_row"
    public const val ACCOUNT_TYPE_DIALOG_TEST_TAG: String = "debug_account_type_dialog"
    public fun accountTypeOption(name: String): String = "debug_account_type_option_${name.lowercase()}"
}
