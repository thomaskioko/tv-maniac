package com.thomaskioko.tvmaniac.data.library.model

public data class LibraryItem(
    val traktId: Long,
    val tmdbId: Long?,
    val title: String,
    val posterPath: String?,
    val status: String?,
    val year: String?,
    val rating: Double?,
    val genres: List<String>?,
    val seasonCount: Long,
    val episodeCount: Long,
    val watchedCount: Long,
    val totalCount: Long,
    val lastWatchedAt: Long?,
    val followedAt: Long?,
    val isFollowed: Boolean,
    val watchProviders: List<WatchProvider> = emptyList(),
)


