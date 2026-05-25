package com.thomaskioko.tvmaniac.myshows.presenter.model

public data class NextEpisodeItem(
    val episodeId: Long,
    val episodeTitle: String,
    val episodeNumberFormatted: String,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val stillPath: String?,
    val firstAired: Long?,
)
