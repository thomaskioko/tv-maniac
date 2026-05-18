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
    val followedAt: Long? = null,
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
    val showTmdbId = showTmdbId ?: return null
    val showName = showName ?: return null
    val episodeId = episodeId ?: return null
    val seasonId = seasonId ?: return null
    val seasonNumber = seasonNumber ?: return null
    val episodeNumber = episodeNumber ?: return null
    return UpNextEpisode(
        showTraktId = showTraktId,
        showTmdbId = showTmdbId,
        showName = showName,
        showPoster = showPoster,
        showStatus = showStatus,
        showYear = showYear,
        episodeId = episodeId,
        episodeName = episodeName,
        seasonId = seasonId,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = runtime,
        stillPath = stillPath,
        overview = overview,
        followedAt = followedAt,
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
