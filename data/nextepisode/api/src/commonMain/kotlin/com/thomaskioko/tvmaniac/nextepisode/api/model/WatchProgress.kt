package com.thomaskioko.tvmaniac.nextepisode.api.model

public data class WatchProgress(
    val showId: Long,
    val totalEpisodesWatched: Int,
    val lastSeasonWatched: Long?,
    val lastEpisodeWatched: Long?,
    val nextEpisode: NextEpisodeWithShow?,
)
