package com.thomaskioko.tvmaniac.episodes.api

public data class WatchedShowBatch(
    public val tmdbId: Long?,
    public val imdbId: String?,
    public val title: String?,
    public val episodes: List<WatchedEpisodeEntry>,
)
