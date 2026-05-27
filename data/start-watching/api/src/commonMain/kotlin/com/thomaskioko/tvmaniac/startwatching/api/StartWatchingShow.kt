package com.thomaskioko.tvmaniac.startwatching.api

public data class StartWatchingShow(
    val traktId: Long,
    val tmdbId: Long,
    val title: String,
    val posterPath: String?,
    val year: String?,
    val inLibrary: Boolean,
    val episodeId: Long? = null,
    val episodeTitle: String? = null,
    val seasonId: Long? = null,
    val seasonNumber: Long? = null,
    val episodeNumber: Long? = null,
    val runtime: Long? = null,
    val episodeStillPath: String? = null,
    val episodeOverview: String? = null,
    val firstAired: Long? = null,
)
