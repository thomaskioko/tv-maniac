package com.thomaskioko.tvmaniac.episodes.api

public data class WatchedShowMetadata(
    public val tmdbId: Long,
    public val runtimeMinutes: Long? = null,
    public val year: String? = null,
    public val genres: List<String>? = null,
)
