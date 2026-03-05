package com.thomaskioko.tvmaniac.discover.presenter.model

public data class NextEpisodeUiModel(
    val showTraktId: Long,
    val showName: String,
    val imageUrl: String?,
    val episodeId: Long,
    val episodeTitle: String,
    val episodeNumberFormatted: String,
    val seasonId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val runtime: String?,
    val overview: String,
    val isNew: Boolean,
    val rating: Double? = null,
    val voteCount: Long? = null,
)
