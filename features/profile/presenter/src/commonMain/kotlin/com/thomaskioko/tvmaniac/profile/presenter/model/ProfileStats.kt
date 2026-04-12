package com.thomaskioko.tvmaniac.profile.presenter.model

public data class ProfileStats(
    val showsWatched: Int,
    val episodesWatched: Int,
    val years: Int = 0,
    val months: Int = 0,
    val days: Int = 0,
    val hours: Int = 0,
    val minutes: Int = 0,
)
