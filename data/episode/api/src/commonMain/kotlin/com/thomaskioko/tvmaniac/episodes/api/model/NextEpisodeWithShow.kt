package com.thomaskioko.tvmaniac.episodes.api.model

public data class NextEpisodeWithShow(
    val showTraktId: Long,
    val showName: String,
    val showPoster: String?,
    val episodeId: Long,
    val episodeName: String?,
    val seasonId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val runtime: Long?,
    val stillPath: String?,
    val overview: String?,
    val followedAt: Long? = null,
    val airDate: String? = null,
    val lastWatchedAt: Long? = null,
)
