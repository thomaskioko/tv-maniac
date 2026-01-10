package com.thomaskioko.tvmaniac.episodes.implementation.model

public data class NextEpisodeKey(
    val showTraktId: Long,
    val seasonNumber: Long = 1,
)
