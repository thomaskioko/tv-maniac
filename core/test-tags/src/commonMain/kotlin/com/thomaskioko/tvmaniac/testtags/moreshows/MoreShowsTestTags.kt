package com.thomaskioko.tvmaniac.testtags.moreshows

public object MoreShowsTestTags {
    public const val SCREEN_TEST_TAG: String = "more_shows_screen"
    public const val GRID_TEST_TAG: String = "more_shows_grid"
    public fun showCard(traktId: Long): String = "more_shows_card_$traktId"
}
