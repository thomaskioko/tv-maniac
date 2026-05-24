package com.thomaskioko.tvmaniac.episodes.api

/**
 * One show's worth of remote watched-episode state from Trakt's bulk
 * `/sync/watched/shows` endpoint. Each entry already carries its own
 * [WatchedEpisodeEntry.showTraktId], so the wrapping `showTraktId` here
 * is redundant for individual rows; it stays at the show level so callers
 * can detect end-of-pagination by checking `result.size` against the
 * requested page limit without flattening first.
 */
public data class WatchedShowBatch(
    public val showTraktId: Long,
    public val episodes: List<WatchedEpisodeEntry>,
)
