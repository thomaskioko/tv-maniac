package com.thomaskioko.tvmaniac.profile.presenter.model

public data class ProfileRecentItem(
    val traktId: Long,
    val tmdbId: Long,
    val title: String,
    val posterUrl: String?,
    val episodeLabel: String,
)
