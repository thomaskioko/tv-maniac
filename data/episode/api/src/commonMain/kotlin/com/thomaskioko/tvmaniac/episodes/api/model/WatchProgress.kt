package com.thomaskioko.tvmaniac.episodes.api.model

public data class WatchProgress(
    val showTraktId: Long,
    val totalEpisodesWatched: Int,
    val lastSeasonWatched: Long?,
    val lastEpisodeWatched: Long?,
    val nextEpisode: NextEpisodeWithShow?,
)
