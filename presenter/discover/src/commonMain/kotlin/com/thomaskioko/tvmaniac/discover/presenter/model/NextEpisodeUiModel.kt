package com.thomaskioko.tvmaniac.discover.presenter.model

data class NextEpisodeUiModel(
    val showId: Long,
    val showName: String,
    val showPoster: String?,
    val episodeId: Long,
    val episodeTitle: String,
    val episodeNumber: String, // "S1E5"
    val runtime: String?, // "45 min"
    val stillImage: String?,
    val overview: String,
    val isNew: Boolean, // Aired in last 7 days
)
