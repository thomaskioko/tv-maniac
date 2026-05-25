package com.thomaskioko.tvmaniac.testtags.startwatching

public object StartWatchingTestTags {
    public const val SCREEN_TEST_TAG: String = "start_watching_screen"
    public const val GRID: String = "start_watching_grid"
    public const val LIST: String = "start_watching_list"
    public const val PROGRESS_INDICATOR: String = "start_watching_loading_indicator"
    public const val EMPTY_STATE: String = "start_watching_empty_state"
    public fun showCard(traktId: Long): String = "start_watching_show_card_$traktId"
}
