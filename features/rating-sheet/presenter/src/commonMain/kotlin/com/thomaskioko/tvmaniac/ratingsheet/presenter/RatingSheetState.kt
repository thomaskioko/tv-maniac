package com.thomaskioko.tvmaniac.ratingsheet.presenter

public data class RatingSheetState(
    val headerLabel: String = "",
    val title: String = "",
    val subtitle: String? = null,
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val scoreLabel: String = "",
    val removeRatingLabel: String = "",
    val userRating: Int? = null,
)
