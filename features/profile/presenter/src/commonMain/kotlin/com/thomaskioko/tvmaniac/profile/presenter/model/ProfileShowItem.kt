package com.thomaskioko.tvmaniac.profile.presenter.model

public data class ProfileShowItem(
    val traktId: Long,
    val tmdbId: Long?,
    val title: String,
    val posterUrl: String?,
)
