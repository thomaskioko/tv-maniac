package com.thomaskioko.tvmaniac.presentation.upnext.model

public data class UpNextEpisodeUiModel(
    val showTraktId: Long,
    val showTmdbId: Long,
    val showName: String,
    val showPoster: String?,
    val showStatus: String?,
    val showYear: String?,
    val episodeId: Long?,
    val episodeName: String?,
    val seasonId: Long?,
    val seasonNumber: Long?,
    val episodeNumber: Long?,
    val runtime: Long?,
    val stillPath: String?,
    val overview: String?,
    val firstAired: Long?,
    val seasonCount: Long,
    val episodeCount: Long,
    val watchedCount: Long,
    val totalCount: Long,
)
