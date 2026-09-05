package com.thomaskioko.tvmaniac.testtags.ratingsheet

public object RatingSheetTestTags {
    public const val SHEET_TEST_TAG: String = "rating_sheet"
    public const val CLEAR_RATING_BUTTON: String = "rating_sheet_clear_rating"
    public fun score(rating: Int): String = "rating_sheet_score_$rating"
}
