package com.thomaskioko.tvmaniac.statistics.presenter.model

public data class MostWatchedShowItem(
    val showId: Long,
    val title: String,
    val posterPath: String?,
    val episodeCount: Long,
    val caption: String,
)
