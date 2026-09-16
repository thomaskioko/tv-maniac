package com.thomaskioko.tvmaniac.testtags.genreshows

public object GenreShowsTestTags {
    public const val SCREEN_TEST_TAG: String = "genre_shows_screen"
    public const val GRID_TEST_TAG: String = "genre_shows_grid"
    public const val RETRY_TEST_TAG: String = "genre_shows_retry"
    public const val SHOW_CARD_TEST_TAG: String = "genre_shows_card"
    public fun showCard(showId: Long): String = "${SHOW_CARD_TEST_TAG}_$showId"
}
