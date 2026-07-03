package com.thomaskioko.tvmaniac.ratingsheet.presenter

public sealed interface RatingSheetAction {
    public data class RatingSelected(val rating: Int) : RatingSheetAction
    public data object RatingCleared : RatingSheetAction
    public data object Dismissed : RatingSheetAction
}
