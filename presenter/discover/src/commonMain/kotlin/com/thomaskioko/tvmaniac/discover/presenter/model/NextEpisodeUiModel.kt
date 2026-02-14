package com.thomaskioko.tvmaniac.discover.presenter.model

public data class NextEpisodeUiModel(
    val showTraktId: Long,
    val showName: String,
    val imageUrl: String?,
    val episodeId: Long,
    val episodeTitle: String,
    val episodeNumberFormatted: String, // "S1E5"
    val seasonId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val runtime: String?, // "45 min"
    val overview: String,
    val isNew: Boolean, // Aired in last 7 days
)
