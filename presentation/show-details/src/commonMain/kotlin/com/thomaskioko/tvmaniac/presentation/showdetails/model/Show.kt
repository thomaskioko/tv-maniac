package com.thomaskioko.tvmaniac.presentation.showdetails.model

data class Show(
    val tmdbId: Long,
    val title: String,
    val posterImageUrl: String?,
    val backdropImageUrl: String?,
    val isInLibrary: Boolean,
)
