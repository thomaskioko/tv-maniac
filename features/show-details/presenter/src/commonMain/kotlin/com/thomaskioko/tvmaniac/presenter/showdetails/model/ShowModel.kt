package com.thomaskioko.tvmaniac.presenter.showdetails.model

public data class ShowModel(
    val traktId: Long,
    val title: String,
    val posterImageUrl: String?,
    val backdropImageUrl: String?,
    val isInLibrary: Boolean,
)
