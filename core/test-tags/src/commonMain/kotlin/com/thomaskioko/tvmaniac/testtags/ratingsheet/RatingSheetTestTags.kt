package com.thomaskioko.tvmaniac.testtags.ratingsheet

public object RatingSheetTestTags {
    public const val SHEET_TEST_TAG: String = "rating_sheet"
    public const val CLEAR_RATING_BUTTON: String = "rating_sheet_clear_rating"
    public fun starRating(rating: Int): String = "rating_sheet_star_$rating"
}
