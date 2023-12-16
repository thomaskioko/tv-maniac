package com.thomaskioko.tvmaniac.presentation.showdetails.model

data class SimilarShow(
    val tmdbId: Long,
    val title: String,
    val posterImageUrl: String?,
    val backdropImageUrl: String?,
)
