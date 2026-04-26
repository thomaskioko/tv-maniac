package com.thomaskioko.tvmaniac.testtags.library

public object LibraryTestTags {
    public const val SCREEN_TEST_TAG: String = "library_screen"
    public const val EMPTY_STATE_TEST_TAG: String = "library_empty_state"
    public fun showRow(traktId: Long): String = "library_show_row_$traktId"
}
