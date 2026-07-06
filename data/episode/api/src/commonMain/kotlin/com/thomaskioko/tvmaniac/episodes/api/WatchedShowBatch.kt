package com.thomaskioko.tvmaniac.episodes.api

import kotlin.time.Instant

public data class WatchedShowBatch(
    public val tmdbId: Long?,
    public val imdbId: String?,
    public val title: String?,
    public val providerShowId: String?,
    public val episodes: List<WatchedEpisodeEntry>,
    public val lastUpdatedAt: Instant? = null,
)
