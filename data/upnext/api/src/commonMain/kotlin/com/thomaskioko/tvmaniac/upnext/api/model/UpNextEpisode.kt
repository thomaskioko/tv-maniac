package com.thomaskioko.tvmaniac.upnext.api.model

public data class UpNextEpisode(
    val showTraktId: Long,
    val showTmdbId: Long,
    val showName: String,
    val showPoster: String?,
    val showStatus: String?,
    val showYear: String?,
    val episodeId: Long,
    val episodeName: String?,
    val seasonId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val runtime: Long?,
    val stillPath: String?,
    val overview: String?,
    val firstAired: Long? = null,
    val lastWatchedAt: Long? = null,
    val seasonCount: Long = 0,
    val episodeCount: Long = 0,
    val watchedCount: Long = 0,
    val totalCount: Long = 0,
    val rating: Double? = null,
    val voteCount: Long? = null,
)

public fun NextEpisodeWithShow.toUpNextEpisode(): UpNextEpisode? {
    val tmdbId = showTmdbId ?: return null
    val name = showName ?: return null
    val resolvedEpisodeId = episodeId ?: return null
    val resolvedSeasonId = seasonId ?: return null
    val resolvedSeasonNumber = seasonNumber ?: return null
    val resolvedEpisodeNumber = episodeNumber ?: return null
    return UpNextEpisode(
        showTraktId = showTraktId,
        showTmdbId = tmdbId,
        showName = name,
        showPoster = showPoster,
        showStatus = showStatus,
        showYear = showYear,
        episodeId = resolvedEpisodeId,
        episodeName = episodeName,
        seasonId = resolvedSeasonId,
        seasonNumber = resolvedSeasonNumber,
        episodeNumber = resolvedEpisodeNumber,
        runtime = runtime,
        stillPath = stillPath,
        overview = overview,
        firstAired = firstAired,
        lastWatchedAt = lastWatchedAt,
        seasonCount = seasonCount,
        episodeCount = episodeCount,
        watchedCount = watchedCount,
        totalCount = totalCount,
        rating = rating,
        voteCount = voteCount,
    )
}
