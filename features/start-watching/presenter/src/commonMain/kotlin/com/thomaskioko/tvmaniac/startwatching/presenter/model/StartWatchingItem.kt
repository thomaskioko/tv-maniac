package com.thomaskioko.tvmaniac.startwatching.presenter.model

public data class StartWatchingItem(
    val traktId: Long,
    val title: String,
    val posterImageUrl: String?,
    val year: String?,
    val episodeId: Long? = null,
    val episodeTitle: String? = null,
    val episodeNumberFormatted: String? = null,
    val seasonNumber: Long? = null,
    val episodeNumber: Long? = null,
    val runtime: String? = null,
    val stillImageUrl: String? = null,
)
