package com.thomaskioko.tvmaniac.data.backup.api

public data class RestoreSummary(
    val showCount: Int,
    val episodeCount: Int,
    val skippedShows: List<String> = emptyList(),
    val skippedSeasonRatings: Int = 0,
    val skippedEpisodeRatings: Int = 0,
    val rewatchSessionsKept: Int = 0,
    val listsNotRestored: Int = 0,
)
