package com.thomaskioko.tvmaniac.watchlist.presenter.model

public data class NextEpisodeItem(
    val episodeId: Long,
    val episodeTitle: String,
    val episodeNumberFormatted: String,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val stillPath: String?,
    val airDate: String?,
)
