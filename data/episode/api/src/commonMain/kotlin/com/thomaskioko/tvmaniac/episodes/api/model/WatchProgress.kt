package com.thomaskioko.tvmaniac.episodes.api.model

import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow

public data class WatchProgress(
    val showTraktId: Long,
    val totalEpisodesWatched: Int,
    val lastSeasonWatched: Long?,
    val lastEpisodeWatched: Long?,
    val nextEpisode: NextEpisodeWithShow?,
)
