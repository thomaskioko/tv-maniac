package com.thomaskioko.tvmaniac.domain.watchlist.model

public data class UpNextSections(
    val watchNext: List<UpNextEpisodeInfo>,
    val stale: List<UpNextEpisodeInfo>,
) {
    public fun filterByQuery(query: String): UpNextSections {
        if (query.isBlank()) return this
        return copy(
            watchNext = watchNext.filter { it.showName.contains(query, ignoreCase = true) },
            stale = stale.filter { it.showName.contains(query, ignoreCase = true) },
        )
    }
}

public data class UpNextEpisodeInfo(
    val showTraktId: Long,
    val showName: String,
    val showPoster: String?,
    val episodeId: Long,
    val episodeTitle: String,
    val episodeNumberFormatted: String,
    val seasonId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val formattedRuntime: String?,
    val stillImage: String?,
    val overview: String,
    val firstAired: Long?,
    val remainingEpisodes: Int,
    val lastWatchedAt: Long?,
    val badge: EpisodeBadge = EpisodeBadge.NONE,
)
