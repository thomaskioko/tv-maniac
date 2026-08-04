package com.thomaskioko.tvmaniac.episodes.api.model

public data class MostWatchedShow(
    val showId: Long,
    val title: String,
    val posterPath: String?,
    val episodeCount: Long,
    val totalRuntimeMinutes: Long,
)
